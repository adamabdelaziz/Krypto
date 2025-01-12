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
import org.adam.kryptobot.feature.swapper.data.model.SwapStrategy
import org.adam.kryptobot.feature.swapper.data.model.TrackedTransaction
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.data.model.TransactionToken
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.SOLANA_MINT_ADDRESS
import org.adam.kryptobot.util.getSwapTokenAddresses
import java.math.BigDecimal
import kotlin.math.pow

interface SwapperRepository {
    fun updateQuoteConfig(update: QuoteParamsConfig.() -> QuoteParamsConfig)
    fun updateStrategy(baseTokenAddress: String, update: SwapStrategy.() -> SwapStrategy)
    fun updateDexSelection(
        dex: Dex,
        addToDexes: Boolean,
        currentConfig: QuoteParamsConfig
    ): QuoteParamsConfig

    fun updateTrackedTransaction(updatedTransaction: TrackedTransaction)
    suspend fun getQuote(
        baseTokenAddress: String?,
        baseTokenSymbol: String?,
        quoteTokenAddress: String?,
        quoteTokenSymbol: String?,
        amount: Double,
        initialPrice: BigDecimal, // this refers to the SOL price of the token you're buying so you know when to swap back for profit
    )

    /*
        TODO: Expose an "auto" parameter for later so that it immediately finishes the swap
         and not just gets the instructions and waits for UX
          May also be able to just pass around the transaction vs the quote and then having to get it each time?
     */
    suspend fun attemptSwap(quote: String)
    suspend fun attemptSwapInstructions()

    suspend fun performSwapTransaction(quote: String, shouldTrack: Boolean)

    val quoteConfig: StateFlow<QuoteParamsConfig>
    val swapStrategies: StateFlow<List<SwapStrategy>>
    val currentSwaps: StateFlow<List<Transaction>>  //Token address key and list of associated swaps done on it
    val currentTrackedTransactions: StateFlow<List<TrackedTransaction>> // After Quote -> Instructions -> Swap occurs. Track it against dex price updates to determine profit
}

class SwapperRepositoryImpl(
    private val json: Json,
    private val stateFlowScope: CoroutineScope,
    private val swapApi: JupiterSwapApi,
    private val solanaApi: SolanaApi,
) : SwapperRepository {

    private val _currentSwaps: MutableStateFlow<List<Transaction>> = MutableStateFlow(listOf())
    override val currentSwaps: StateFlow<List<Transaction>> =
        _currentSwaps.stateIn(
            scope = stateFlowScope,
            initialValue = _currentSwaps.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _currentTrackedTransactions: MutableStateFlow<List<TrackedTransaction>> = MutableStateFlow(listOf())
    override val currentTrackedTransactions: StateFlow<List<TrackedTransaction>> =
        _currentTrackedTransactions.stateIn(
            scope = stateFlowScope,
            initialValue = _currentTrackedTransactions.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _swapStrategies: MutableStateFlow<List<SwapStrategy>> = MutableStateFlow(listOf())
    override val swapStrategies: StateFlow<List<SwapStrategy>> =
        _swapStrategies.stateIn(
            scope = stateFlowScope,
            initialValue = _swapStrategies.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _quoteConfig: MutableStateFlow<QuoteParamsConfig> = MutableStateFlow(QuoteParamsConfig())
    override val quoteConfig: StateFlow<QuoteParamsConfig> =
        _quoteConfig.stateIn(
            scope = stateFlowScope,
            initialValue = _quoteConfig.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    override fun updateQuoteConfig(update: QuoteParamsConfig.() -> QuoteParamsConfig) {
        _quoteConfig.value = _quoteConfig.value.update()
    }

    override fun updateStrategy(baseTokenAddress: String, update: SwapStrategy.() -> SwapStrategy) {
        val list = _swapStrategies.value.toMutableList()
        val updatedStrategyList = list.map {
            if (it.key == baseTokenAddress) {
                it.update()
            } else {
                it.copy()
            }
        }.toMutableList()

        if (updatedStrategyList.none { it.key == baseTokenAddress }) {
            updatedStrategyList.add(SwapStrategy(key = baseTokenAddress).update())
        }

        _swapStrategies.value = emptyList()
        _swapStrategies.value = updatedStrategyList
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
        initialPrice: BigDecimal,
    ) {
        if (baseTokenAddress == null || quoteTokenAddress == null) return

        Logger.d("Initial price is $initialPrice")
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
                val outDecimals = solanaApi.getMintDecimalsAmount(outputAddress)

                Logger.d("Decimals for quote input $decimals for output $outDecimals")

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

                Logger.d("Raw amounts In: ${quoteDto?.inAmount} Out: ${quoteDto?.outAmount}")

                val readableIn = adjustTokenAmount(quoteDto?.inAmount ?: "0", decimals)
                val readableOut = adjustTokenAmount(quoteDto?.outAmount ?: "0", outDecimals)

                Logger.d("Human readable attempt in: $readableIn")
                Logger.d("Human readable attempt out: $readableOut")

                Logger.d("In mint ${quoteDto?.inputMint} is Solana ${quoteDto?.inputMint == SOLANA_MINT_ADDRESS}")
                Logger.d("Out mint ${quoteDto?.outputMint} is Solana ${quoteDto?.outputMint == SOLANA_MINT_ADDRESS}")

                val inputToken = TransactionToken(
                    symbol = inputSymbol,
                    address = inputAddress,
                    amount = readableIn,
                    amountLamports = quoteDto?.inAmount?.toLong() ?: 0,
                    decimals = decimals
                )

                val outputToken = TransactionToken(
                    symbol = outputSymbol,
                    address = outputAddress,
                    amount = readableOut,
                    amountLamports = quoteDto?.outAmount?.toLong() ?: 0,
                    decimals = outDecimals
                )

                quoteRaw?.let {
                    createTransaction(
                        quote = it,
                        amount = amount,
                        quoteDto = quoteDto,
                        inputToken = inputToken,
                        outputToken = outputToken,
                        swapMode = _quoteConfig.value.swapMode,
                        initialPrice = initialPrice
                    )
                }
            } catch (e: Exception) {
                Logger.d("Exception getting Quote ${e.message}")
            }
        }
    }

    private fun formatTokenAmountForQuote(amount: Double, decimals: Int): String {
        return ((amount * 10.0.pow(decimals)).toLong()).toString()
    }

    private fun adjustTokenAmount(amount: String, decimals: Int): BigDecimal {
        return BigDecimal(amount).movePointLeft(decimals)
    }

    private suspend fun createTransaction(
        quote: String,
        amount: Double,
        quoteDto: JupiterQuoteDto?,
        inputToken: TransactionToken,
        outputToken: TransactionToken,
        swapMode: SwapMode,
        initialPrice: BigDecimal,
    ) {
        val swapList = _currentSwaps.value.toMutableList()

        val fee = quoteDto?.let {
            Logger.d("Platform fee null: ${it.platformFee == null}")
            getTotalFees(it, inputToken.decimals)
        } ?: BigDecimal.ZERO

        Logger.d("Readable fee is $fee")

        val transaction = Transaction(
            quoteRaw = quote,
            amount = amount,
            quoteDto = quoteDto,
            swapMode = swapMode,
            inToken = inputToken,
            outToken = outputToken,
            fee = fee,
            initialDexPriceSol = initialPrice
        )

        Logger.d("Determined price is ${transaction.initialMonkasSol} dex price is $initialPrice")

        swapList.add(transaction)
        _currentSwaps.value = swapList.toList()

        attemptSwap(quote)
    }

    private fun updateTransaction(updatedTransaction: Transaction) {
        val swapList = _currentSwaps.value.toMutableList()

        val updatedSwapList = swapList.map {
            if (it.quoteRaw == updatedTransaction.quoteRaw) updatedTransaction else it.copy()
        }.toMutableList()

        _currentSwaps.value = emptyList()
        _currentSwaps.value = updatedSwapList.toList()
    }

    private fun getTransaction(quote: String): Transaction? {
        return _currentSwaps.value.firstOrNull { it.quoteRaw == quote }
    }

    override fun updateTrackedTransaction(updatedTransaction: TrackedTransaction) {
        val trackedList = _currentTrackedTransactions.value.toMutableList()

        val updatedList = trackedList.map {
            if (it.transaction.outToken.address == updatedTransaction.transaction.outToken.address)
                updatedTransaction
            else
                it.copy()
        }.toMutableList()

        _currentTrackedTransactions.value = emptyList()
        _currentTrackedTransactions.value = updatedList.toList()
    }

    private fun getTrackedTransaction(baseTokenAddress: String): TrackedTransaction? {
        return _currentTrackedTransactions.value.firstOrNull { it.transaction.outToken.address == baseTokenAddress }
    }

    private fun getTotalFees(quote: JupiterQuoteDto, inputDecimals: Int): BigDecimal {
        val routeFees = quote.routePlan.map {
            val feeDecimals = solanaApi.getMintDecimalsAmount(it.swapInfo.feeMint)
            it.swapInfo.feeAmount.toBigDecimal().movePointLeft(feeDecimals)
        }.fold(BigDecimal.ZERO) { acc, fee -> acc + fee }

        val platformFee = quote.platformFee?.amount?.toBigDecimal()?.movePointLeft(inputDecimals) ?: BigDecimal.ZERO
        Logger.d("Platform fee is $platformFee")
        return routeFees + platformFee
    }

    /*
        This works as the step after quote
        Will need to pass around quote and token address to ensure its referring to the correct quote in the List<Transaction>
        TODO: Likely rename function since its not actually performing the swap just getting the instructions.
                GET KEY FROM WALLET AND NOT CONSTANTS
     */
    override suspend fun attemptSwap(quote: String) {
        val transaction = getTransaction(quote)
        withContext(Dispatchers.IO) {
            try {
                transaction?.let { tx ->
                    val instructions = swapApi.swapTokens(
                        quoteResponse = tx.quoteRaw,
                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
                    )
                    instructions?.let {
                        val updatedTransaction = tx.copy(swapResponse = it, transactionStep = TransactionStep.INSTRUCTIONS_MADE)
                        updateTransaction(updatedTransaction)
                        if (!_quoteConfig.value.safeMode) {
                            performSwapTransaction(updatedTransaction.quoteRaw, true)
                        }
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
    override suspend fun performSwapTransaction(quote: String, shouldTrack: Boolean) {
        val transaction = getTransaction(quote)
        transaction?.swapResponse?.let {
            withContext(Dispatchers.IO) {
                try {
                    val result = solanaApi.performSwapTransaction(
                        privateKey = SECOND_WALLET_PRIVATE_KEY,
                        instructions = it.swapTransaction
                    )

                    val updatedTransaction = transaction.copy(
                        transactionSignature = result.getOrNull(),
                        status = if (result.isSuccess) Status.SUCCESS else Status.FAIL
                    )
                    updateTransaction(updatedTransaction)

                    if (shouldTrack) {
                        val trackedTransaction = TrackedTransaction(
                            transaction = updatedTransaction,
                            highestObservedPriceSol = updatedTransaction.initialDexPriceSol,
                            isCompleted = false,
                        )
                        val list = _currentTrackedTransactions.value.toMutableList()
                        list.add(trackedTransaction)
                        _currentTrackedTransactions.value = list.toList()
                    }
                } catch (e: Exception) {
                    Logger.d("Exception performing swap transaction ${e.message}")
                    e.printStackTrace()
                }
            }
        } ?: run {
            Logger.d("Null swap transaction")
        }
    }

    private fun determineSwapAmount(transaction: Transaction, currentPrice: BigDecimal, strategy: SwapStrategy): BigDecimal {
        val totalTokens = BigDecimal(transaction.amount)

        return when (strategy.exitStrategy) {
            SwapStrategy.ExitStrategy.SWAP_ALL -> totalTokens

            SwapStrategy.ExitStrategy.SWAP_PARTIAL -> {
                val percentage = strategy.exitPct ?: BigDecimal(100)
                totalTokens * (percentage / BigDecimal(100))
            }

            SwapStrategy.ExitStrategy.BREAK_EVEN -> {
                val initialSolSpent = transaction.initialDexPriceSol * totalTokens
                if (currentPrice * totalTokens >= initialSolSpent) {
                    initialSolSpent / currentPrice
                } else {
                    BigDecimal.ZERO
                }
            }
        }
    }

}