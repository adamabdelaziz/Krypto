package org.adam.kryptobot.feature.swapper.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.swapper.repository.SwapperRepository
import org.adam.kryptobot.feature.wallet.repository.WalletRepository

class SwapperScreenModel(
    private val swapperRepository: SwapperRepository,
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
) : ScreenModel {

    val uiState: StateFlow<SwapperScreenUiState> = combine(
        scannerRepository.latestDexPairs,
        swapperRepository.currentQuotes,
        swapperRepository.currentSwapInstructions,
        swapperRepository.currentSwapResponse,
        ) { tokens, quote, swapInstructions, swapResponse, ->
        SwapperScreenUiState(
            quote = quote,
            pair = null,
            swapInstructions = swapInstructions,
            swapResponse = swapResponse,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SwapperScreenUiState()
    )

    fun onEvent(event: SwapperScreenEvent) {
        when (event) {
            SwapperScreenEvent.OnGenerateDebugWalletClicked -> {
                screenModelScope.launch {
                    walletRepository.refreshBalance()
                }
            }

            is SwapperScreenEvent.OnDexPairClicked -> {
                getQuote(event.dexPair)
            }

            SwapperScreenEvent.OnGenerateSwapInstructionsClicked -> {
                screenModelScope.launch {
                    swapperRepository.attemptSwap()
                }
            }

            SwapperScreenEvent.OnPerformSwapTransactionClicked -> {
                screenModelScope.launch {
                    swapperRepository.performSwapTransaction()
                }
            }
        }
    }

    private fun getQuote(dexPair: DexPair) {
        screenModelScope.launch {
            swapperRepository.getQuote(
                inputAddress = dexPair.quoteToken?.address ?: "",
                outputAddress = dexPair.baseToken?.address ?: "",
                amount = 0.001,
            )
        }
    }
}