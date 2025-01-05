package org.adam.kryptobot.feature.swapper.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.data.JupiterSwapApi
import org.adam.kryptobot.feature.swapper.data.SolanaApi
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.model.QuoteParamsConfig
import org.adam.kryptobot.feature.swapper.data.model.TransactionStep
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import kotlin.math.pow

interface SwapperRepository {
    fun updateQuoteConfig(update: QuoteParamsConfig.() -> QuoteParamsConfig)
    fun updateDexSelection(
        dex: Dex,
        addToDexes: Boolean,
        currentConfig: QuoteParamsConfig
    ): QuoteParamsConfig

    /*
        TODO: Condense params here and just refer to values in the function
     */
    suspend fun getQuote(
        baseTokenAddress: String?,
        quoteTokenAddress: String?,
        amount: Double,
    )

    suspend fun attemptSwap(quote: String, tokenAddress: String)
    suspend fun attemptSwapInstructions()

    suspend fun performSwapTransaction(quote: String, tokenAddress: String)

    //TODO change for support for multiple DEX pairs at once(key field in data object instead of maps)
    val quoteConfig: StateFlow<QuoteParamsConfig>
    val currentSwaps: StateFlow<Map<String, List<TransactionStep>>>  //Token address key and list of associated swaps done on it
}

class SwapperRepositoryImpl(
    private val json: Json,
    private val stateFlowScope: CoroutineScope,
    private val swapApi: JupiterSwapApi,
    private val solanaApi: SolanaApi,
) : SwapperRepository {

    private val _currentSwaps: MutableStateFlow<Map<String, List<TransactionStep>>> =
        MutableStateFlow(
            mapOf()
        )
    override val currentSwaps: StateFlow<Map<String, List<TransactionStep>>> =
        _currentSwaps.stateIn(
            scope = stateFlowScope,
            initialValue = _currentSwaps.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _quoteConfig: MutableStateFlow<QuoteParamsConfig> = MutableStateFlow(
        QuoteParamsConfig()
    )
    override val quoteConfig: StateFlow<QuoteParamsConfig> =
        _quoteConfig.stateIn(
            scope = stateFlowScope,
            initialValue = _quoteConfig.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    override fun updateQuoteConfig(update: QuoteParamsConfig.() -> QuoteParamsConfig) {
        _quoteConfig.value = _quoteConfig.value.update()
    }

    override fun updateDexSelection(
        dex: Dex,
        addToDexes: Boolean,
        currentConfig: QuoteParamsConfig
    ): QuoteParamsConfig {
        return if (addToDexes) {
            currentConfig.copy(
                dexes = currentConfig.dexes + dex,
                excludeDexes = currentConfig.excludeDexes - dex
            )
        } else {
            currentConfig.copy(
                dexes = currentConfig.dexes - dex,
                excludeDexes = currentConfig.excludeDexes + dex
            )
        }
    }

    override suspend fun getQuote(
        baseTokenAddress: String?,
        quoteTokenAddress: String?,
        amount: Double,
    ) {
        if (baseTokenAddress == null || quoteTokenAddress == null) return

        val (inputAddress, outputAddress) = when (quoteConfig.value.swapMode) {
            SwapMode.ExactIn -> {
                (quoteTokenAddress to baseTokenAddress)
            }

            SwapMode.ExactOut -> {
                (baseTokenAddress to quoteTokenAddress)
            }
        }
        withContext(Dispatchers.IO) {
            try {
                val decimals = solanaApi.getMintDecimalsAmount(baseTokenAddress)
                Logger.d("Decimals for quote is $decimals")
                val (quoteRaw, quoteDto) = swapApi.getQuote(
                    inputAddress = inputAddress,
                    outputAddress = outputAddress,
                    amount = formatTokenAmountForQuote(amount, decimals),
                    slippageBps = quoteConfig.value.slippageBps,
                    swapMode = quoteConfig.value.swapMode.name,
                    dexes = quoteConfig.value.dexes.map { it.name },
                    excludeDexes = quoteConfig.value.excludeDexes.map { it.name },
                    restrictIntermediateTokens = quoteConfig.value.restrictIntermediateTokens,
                    onlyDirectRoutes = quoteConfig.value.onlyDirectRoutes,
                    asLegacyTransaction = quoteConfig.value.asLegacyTransaction,
                    platformFeeBps = quoteConfig.value.platformFeeBps,
                    maxAccounts = quoteConfig.value.maxAccounts,
                    autoSlippage = quoteConfig.value.autoSlippage,
                    maxAutoSlippageBps = quoteConfig.value.maxAutoSlippageBps,
                    autoSlippageCollisionUsdValue = quoteConfig.value.autoSlippageCollisionUsdValue
                )

                quoteRaw?.let {
                    createTransactionStep(
                        quote = it,
                        inputAddress = baseTokenAddress,
                        amount = amount,
                        quoteDto = quoteDto
                    )
                }
                //  _currentQuote.value = it


            } catch (e: Exception) {
                Logger.d("Exception getting Quote ${e.message}")
            }
        }
    }

    private fun formatTokenAmountForQuote(amount: Double, decimals: Int): String =
        ((amount * 10.0.pow(decimals)).toLong()).toString()

    private fun createTransactionStep(quote: String, inputAddress: String, amount: Double, quoteDto: JupiterQuoteDto?) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap.getOrDefault(inputAddress, emptyList()).toMutableList()

        val transactionStep = TransactionStep(quoteRaw = quote, amount = amount, quoteDto = quoteDto)
        swapList.add(transactionStep)

        currentMap[inputAddress] = swapList.toList()
        _currentSwaps.value = currentMap.toMap()
    }

    private fun updateTransactionStep(tokenAddress: String, updatedStep: TransactionStep) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap[tokenAddress]?.toMutableList() ?: return

        val index = swapList.indexOfFirst { it.quoteRaw == updatedStep.quoteRaw }
        if (index != -1) {
            swapList[index] = updatedStep
            currentMap[tokenAddress] = swapList.toList()
            _currentSwaps.value = currentMap.toMap()
        }
    }

    private fun getTransactionStep(quote: String, tokenAddress: String): TransactionStep? {
        return _currentSwaps.value[tokenAddress]?.firstOrNull { it.quoteRaw == quote }
    }

    /*
        This works as the step after quote
        Will need to pass around quote and token address to ensure its referring to the correct quote in the List<TransactionStep>
     */
    override suspend fun attemptSwap(quote: String, tokenAddress: String) {
        val transactionStep = getTransactionStep(quote, tokenAddress)
        withContext(Dispatchers.IO) {
            try {
                transactionStep?.let { step ->
                    val instructions = swapApi.swapTokens(
                        quoteResponse = step.quoteRaw,
                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
                    )
                    instructions?.let {
                        val updatedStep = step.copy(swapResponse = it)
                        updateTransactionStep(tokenAddress, updatedStep)
                        Logger.d("Instructions are $it")
                        //val encoding = detectEncoding(it.swapTransaction)
                        //Logger.d("Encoding is $encoding")
                    } ?: run {
                        Logger.d("Null instructions")
                    }
                } ?: run {
                    Logger.d("Null current quote")
                }
            } catch (e: Exception) {
                Logger.d("Exception getting instructions ${e.message}")
            }
        }
    }

    override suspend fun attemptSwapInstructions() {
//        withContext(Dispatchers.IO) {
//            try {
//                _currentQuote.value?.let {
//                    val instructions = swapApi.swapInstructions(
//                        quoteResponse = it,
//                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
//                    )
//                    instructions?.let {
//                        //Logger.d("Instructions are $it")
//                        _currentSwapInstructions.value = it
//                    } ?: run {
//                        Logger.d("Null instructions")
//                    }
//                } ?: run {
//                    Logger.d("Null current quote")
//                }
//            } catch (e: Exception) {
//                Logger.d("Exception getting instructions ${e.message}")
//            }
//        }
    }

    /*
        This is the one that works
        Similarly will need the specific quote and token address
     */
    override suspend fun performSwapTransaction(quote: String, tokenAddress: String) {
        val transactionStep = getTransactionStep(quote, tokenAddress)
        transactionStep?.swapResponse?.let {
            withContext(Dispatchers.IO) {
                try {
                    val result = solanaApi.performSwapTransaction(
                        privateKey = SECOND_WALLET_PRIVATE_KEY,
                        instructions = it.swapTransaction
                    )

                    val updatedStep = transactionStep.copy(
                        transactionSignature = result.getOrNull(),
                        status = if (result.isSuccess) Status.SUCCESS else Status.FAIL
                    )
                    updateTransactionStep(tokenAddress, updatedStep)
                } catch (e: Exception) {
                    Logger.d("Exception performing swap transaction ${e.message}")
                    e.printStackTrace()
                }
            }
        } ?: run {
            Logger.d("Null swap transaction")
        }
    }


}