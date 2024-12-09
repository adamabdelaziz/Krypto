package org.adam.kryptobot.feature.swapper.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.adam.kryptobot.feature.swapper.data.JupiterSwapApi
import org.adam.kryptobot.feature.swapper.data.Sol4kApi
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.data.model.Wallet
import kotlin.math.pow

interface SwapperRepository {
    suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: Double,
        slippageBps: Int = 50,
        swapMode: String = "ExactIn",  // Default to ExactIn
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

    suspend fun attemptSwap()
    suspend fun attemptSwapInstructions()

    fun createDebugWallet()

    val currentWallet: StateFlow<Wallet>
    val currentQuotes: StateFlow<JupiterQuoteDto?>
    val currentSwapInstructions: StateFlow<JupiterSwapInstructionsDto?>
    val currentSwapResponse: StateFlow<JupiterSwapResponseDto?>
}

class SwapperRepositoryImpl(
    private val stateFlowScope: CoroutineScope,
    private val swapApi: JupiterSwapApi,
    private val solanaApi: Sol4kApi,
) : SwapperRepository {

    //TODO actual wallet access
    private val _currentWallet: MutableStateFlow<Wallet> = MutableStateFlow(
        Wallet(
            publicKey = "",
            privateAddress = "",
        )
    )
    override val currentWallet: StateFlow<Wallet> = _currentWallet.stateIn(
        scope = stateFlowScope,
        initialValue = _currentWallet.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    private val _currentQuote: MutableStateFlow<JupiterQuoteDto?> = MutableStateFlow(null)
    override val currentQuotes: StateFlow<JupiterQuoteDto?> = _currentQuote.stateIn(
        scope = stateFlowScope,
        initialValue = _currentQuote.value,
        started = SharingStarted.WhileSubscribed(5000),
    )

    private val _currentSwapInstructions: MutableStateFlow<JupiterSwapInstructionsDto?> =
        MutableStateFlow(null)
    override val currentSwapInstructions: StateFlow<JupiterSwapInstructionsDto?> =
        _currentSwapInstructions.stateIn(
            scope = stateFlowScope,
            initialValue = _currentSwapInstructions.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val _currentSwapResponse: MutableStateFlow<JupiterSwapResponseDto?> =
        MutableStateFlow(null)
    override val currentSwapResponse: StateFlow<JupiterSwapResponseDto?> =
        _currentSwapResponse.stateIn(
            scope = stateFlowScope,
            initialValue = _currentSwapResponse.value,
            started = SharingStarted.WhileSubscribed(5000),
        )

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
                Logger.d("Success getting quote response is $response")
                response?.let {
                    _currentQuote.value = it
                }

            } catch (e: Exception) {
                Logger.d("Exception getting Quote ${e.message}")
            }
        }
    }

    private fun formatTokenAmountForQuote(amount: Double, decimals: Int): String =
        ((amount * 10.0.pow(decimals)).toLong()).toString()

    override suspend fun attemptSwap() {

    }

    override suspend fun attemptSwapInstructions() {

    }

    override fun createDebugWallet() {
        solanaApi.restoreWalletFromPrivateKey()
    }
}