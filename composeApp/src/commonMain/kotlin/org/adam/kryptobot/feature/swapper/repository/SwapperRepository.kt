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
import org.adam.kryptobot.feature.swapper.data.JupiterSwapApi
import org.adam.kryptobot.feature.swapper.data.SolanaApi
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.detectEncoding
import kotlin.math.pow

interface SwapperRepository {
    suspend fun getQuote(
        inputAddress: String,
        outputAddress: String,
        amount: Double,
        slippageBps: Int = 10000,
        swapMode: String = "ExactOut",  // Default to ExactIn? ExactOut worked though
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

    suspend fun performSwapTransaction()

    //TODO change for support for multiple DEX pairs at once(key field in data object instead of maps)
    val currentQuotes: StateFlow<String?>
    val currentSwapInstructions: StateFlow<JupiterSwapInstructionsDto?>
    val currentSwapResponse: StateFlow<JupiterSwapResponseDto?>
}

class SwapperRepositoryImpl(
    private val json: Json,
    private val stateFlowScope: CoroutineScope,
    private val swapApi: JupiterSwapApi,
    private val solanaApi: SolanaApi,
) : SwapperRepository {

    //TODO matching quote with the pair its from
    private val _currentQuote: MutableStateFlow<String?> = MutableStateFlow(null)
    override val currentQuotes: StateFlow<String?> = _currentQuote.stateIn(
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
        withContext(Dispatchers.IO) {
            try {
                _currentQuote.value?.let {
                    val instructions = swapApi.swapTokens(
                        quoteResponse = it,
                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
                    )
                    instructions?.let {
                        _currentSwapResponse.value = it
                        Logger.d("Instructions are $it")
                        val encoding = detectEncoding(it.swapTransaction)
                        Logger.d("Encoding is $encoding")
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
        withContext(Dispatchers.IO) {
            try {
                _currentQuote.value?.let {
                    val instructions = swapApi.swapInstructions(
                        quoteResponse = it,
                        userPublicKey = SECOND_WALLET_PUBLIC_KEY
                    )
                    instructions?.let {
                        //Logger.d("Instructions are $it")
                        _currentSwapInstructions.value = it
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

    override suspend fun performSwapTransaction() {
        _currentSwapResponse.value?.swapTransaction?.let {
            withContext(Dispatchers.IO) {
                try {
                    solanaApi.performSwapTransaction(
                        privateKey = SECOND_WALLET_PRIVATE_KEY,
                        instructions = it
                    )
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