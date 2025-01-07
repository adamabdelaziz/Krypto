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
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.formatToDecimalString
import org.adam.kryptobot.util.getSwapTokenAddresses
import org.adam.kryptobot.util.lamportsToSol
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
        baseTokenSymbol: String?,
        quoteTokenAddress: String?,
        quoteTokenSymbol: String?,
        amount: Double,
    )

    suspend fun attemptSwap(quote: String, tokenAddress: String)
    suspend fun attemptSwapInstructions()

    suspend fun performSwapTransaction(quote: String, tokenAddress: String)

    //TODO change for support for multiple DEX pairs at once(key field in data object instead of maps)
    val quoteConfig: StateFlow<QuoteParamsConfig>
    val currentSwaps: StateFlow<Map<String, List<Transaction>>>  //Token address key and list of associated swaps done on it
}

class SwapperRepositoryImpl(
    private val json: Json,
    private val stateFlowScope: CoroutineScope,
    private val swapApi: JupiterSwapApi,
    private val solanaApi: SolanaApi,
) : SwapperRepository {

    private val _currentSwaps: MutableStateFlow<Map<String, List<Transaction>>> =
        MutableStateFlow(
            mapOf()
        )
    override val currentSwaps: StateFlow<Map<String, List<Transaction>>> =
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
        baseTokenSymbol: String?,
        quoteTokenAddress: String?,
        quoteTokenSymbol: String?,
        amount: Double,
    ) {
        if (baseTokenAddress == null || quoteTokenAddress == null) return

        val (inputAddress, outputAddress) = getSwapTokenAddresses(
            swapMode = _quoteConfig.value.swapMode,
            baseTokenAddress = baseTokenAddress,
            quoteTokenAddress = quoteTokenAddress
        )
        val (inputSymbol, outputSymbol) = getSwapTokenAddresses(
            swapMode = _quoteConfig.value.swapMode,
            baseTokenAddress = baseTokenSymbol ?: "",
            quoteTokenAddress = quoteTokenSymbol ?: ""
        )

        withContext(Dispatchers.IO) {
            try {
                val decimals = solanaApi.getMintDecimalsAmount(inputAddress)
                Logger.d("Decimals for quote is $decimals")
                val (quoteRaw, quoteDto) = swapApi.getQuote(
                    inputAddress = inputAddress,
                    outputAddress = outputAddress,
                    amount = formatTokenAmountForQuote(amount, decimals),
                    slippageBps = _quoteConfig.value.slippageBps,
                    swapMode = _quoteConfig.value.swapMode.name,
                    dexes = _quoteConfig.value.dexes.map { it.name },
                    excludeDexes = _quoteConfig.value.excludeDexes.map { it.name },
                    restrictIntermediateTokens = _quoteConfig.value.restrictIntermediateTokens,
                    onlyDirectRoutes = _quoteConfig.value.onlyDirectRoutes,
                    asLegacyTransaction = _quoteConfig.value.asLegacyTransaction,
                    platformFeeBps = _quoteConfig.value.platformFeeBps,
                    maxAccounts = _quoteConfig.value.maxAccounts,
                    autoSlippage = _quoteConfig.value.autoSlippage,
                    maxAutoSlippageBps = _quoteConfig.value.maxAutoSlippageBps,
                    autoSlippageCollisionUsdValue = _quoteConfig.value.autoSlippageCollisionUsdValue
                )
                quoteDto?.routePlan?.forEach {
                    Logger.d("${it.swapInfo.feeMint} : ${it.swapInfo.feeAmount} : ${it.swapInfo.ammKey} : ${it.swapInfo.label}}")
                }

                quoteRaw?.let {
                    createTransaction(
                        quote = it,
                        baseTokenAddress = baseTokenAddress,
                        amount = amount,
                        quoteDto = quoteDto,
                        inputSymbol = inputSymbol,
                        outputSymbol = outputSymbol,
                        inputAddress = inputAddress,
                        outputAddress = outputAddress,
                        inputAmount = quoteDto?.inAmount ?: "",
                        outputAmount = quoteDto?.outAmount ?: "",
                        swapMode = _quoteConfig.value.swapMode
                    )
                }
            } catch (e: Exception) {
                Logger.d("Exception getting Quote ${e.message}")
            }
        }
    }

    private fun formatTokenAmountForQuote(amount: Double, decimals: Int): String =
        ((amount * 10.0.pow(decimals)).toLong()).toString()

    private fun createTransaction(
        quote: String,
        baseTokenAddress: String,
        amount: Double,
        quoteDto: JupiterQuoteDto?,
        inputSymbol: String,
        outputSymbol: String,
        inputAddress: String,
        outputAddress: String,
        inputAmount: String,
        outputAmount: String,
        swapMode: SwapMode
    ) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap.getOrDefault(baseTokenAddress, emptyList()).toMutableList()

        val transaction = Transaction(
            quoteRaw = quote,
            amount = amount,
            quoteDto = quoteDto,
            inputSymbol = inputSymbol,
            outputSymbol = outputSymbol,
            inputAddress = inputAddress,
            outputAddress = outputAddress,
            swapMode = swapMode,
            inputAmount = inputAmount,
            outputAmount = outputAmount,
        )
        swapList.add(transaction)

        currentMap[baseTokenAddress] = swapList.toList()
        _currentSwaps.value = currentMap.toMap()
    }

    private fun updateTransaction(tokenAddress: String, updatedTransaction: Transaction) {
        val currentMap = _currentSwaps.value.toMutableMap()
        val swapList = currentMap[tokenAddress]?.toMutableList() ?: return

        val index = swapList.indexOfFirst { it.quoteRaw == updatedTransaction.quoteRaw }
        if (index != -1) {
            swapList[index] = updatedTransaction
            currentMap[tokenAddress] = swapList.toList()
            _currentSwaps.value = currentMap.toMap()
        }
    }

    private fun getTransaction(quote: String, tokenAddress: String): Transaction? {
        return _currentSwaps.value[tokenAddress]?.firstOrNull { it.quoteRaw == quote }
    }

    /*
        This works as the step after quote
        Will need to pass around quote and token address to ensure its referring to the correct quote in the List<TransactionStep>
     */
    override suspend fun attemptSwap(quote: String, tokenAddress: String) {
        val transactionStep = getTransaction(quote, tokenAddress)
        withContext(Dispatchers.IO) {
            try {
                transactionStep?.let { step ->
                    val instructions = swapApi.swapTokens(
                        quoteResponse = step.quoteRaw,
                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
                    )
                    instructions?.let {
                        val updatedStep = step.copy(swapResponse = it)
                        updateTransaction(tokenAddress, updatedStep)
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
        val transactionStep = getTransaction(quote, tokenAddress)
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
                    updateTransaction(tokenAddress, updatedStep)
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