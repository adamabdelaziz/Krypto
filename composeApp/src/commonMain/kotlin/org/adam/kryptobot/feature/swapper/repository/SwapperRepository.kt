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
    suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: Double,
        slippageBps: Int = 10000,
        swapMode: String = SwapMode.ExactOut.name,
        dexes: List<String>? = null,
        excludeDexes: List<String>? = null,
        restrictIntermediateTokens: Boolean = false,
        onlyDirectRoutes: Boolean = false,
        asLegacyTransaction: Boolean = false,
        platformFeeBps: Int? = null,
        maxAccounts: Int? = null,
        autoSlippage: Boolean = false,
        maxAutoSlippageBps: Int? = null,
        autoSlippageCollisionUsdValue: Int? = null
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
        inputAddress: String,
        outputAddress: String,
        amount: Double,
        slippageBps: Int,
        swapMode: String,
        dexes: List<String>?,
        excludeDexes: List<String>?,
        restrictIntermediateTokens: Boolean,
        onlyDirectRoutes: Boolean,
        asLegacyTransaction: Boolean,
        platformFeeBps: Int?,
        maxAccounts: Int?,
        autoSlippage: Boolean,
        maxAutoSlippageBps: Int?,
        autoSlippageCollisionUsdValue: Int?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val decimals = solanaApi.getMintDecimalsAmount(inputAddress)
                Logger.d("Decimals for quote is $decimals")
                val response = swapApi.getQuote(
                    inputAddress = inputAddress,
                    outputAddress = outputAddress,
                    amount = formatTokenAmountForQuote(amount, decimals),
                    slippageBps = slippageBps,
                    swapMode = swapMode,
                    dexes = dexes,
                    excludeDexes = excludeDexes,
                    restrictIntermediateTokens = restrictIntermediateTokens,
                    onlyDirectRoutes = onlyDirectRoutes,
                    asLegacyTransaction = asLegacyTransaction,
                    platformFeeBps = platformFeeBps,
                    maxAccounts = maxAccounts,
                    autoSlippage = autoSlippage,
                    maxAutoSlippageBps = maxAutoSlippageBps,
                    autoSlippageCollisionUsdValue = autoSlippageCollisionUsdValue
                )
                //Logger.d("Success getting quote response is $response")
                response?.let {
                    //  _currentQuote.value = it
                    createTransactionStep(
                        quote = it,
                        inputAddress = inputAddress,
                        amount = amount
                    )
                }

            } catch (e: Exception) {
                Logger.d("Exception getting Quote ${e.message}")
            }
        }
    }

    private fun formatTokenAmountForQuote(amount: Double, decimals: Int): String =
        ((amount * 10.0.pow(decimals)).toLong()).toString()

    private fun createTransactionStep(quote: String, inputAddress: String, amount: Double) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap.getOrDefault(inputAddress, emptyList()).toMutableList()

        val transactionStep = TransactionStep(quote = quote, amount = amount)
        swapList.add(transactionStep)

        currentMap[inputAddress] = swapList.toList()
        _currentSwaps.value = currentMap.toMap()
    }

    private fun updateTransactionStep(tokenAddress: String, updatedStep: TransactionStep) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap[tokenAddress]?.toMutableList() ?: return

        val index = swapList.indexOfFirst { it.quote == updatedStep.quote }
        if (index != -1) {
            swapList[index] = updatedStep
            currentMap[tokenAddress] = swapList.toList()
            _currentSwaps.value = currentMap.toMap()
        }
    }

    private fun getTransactionStep(quote: String, tokenAddress: String): TransactionStep? {
        return _currentSwaps.value[tokenAddress]?.firstOrNull { it.quote == quote }
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
                        quoteResponse = step.quote,
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