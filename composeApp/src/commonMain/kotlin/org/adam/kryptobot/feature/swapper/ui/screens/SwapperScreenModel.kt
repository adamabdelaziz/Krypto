package org.adam.kryptobot.feature.swapper.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zhuinden.flowcombinetuplekt.combineStates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.feature.swapper.repository.SwapperRepository
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel
import org.adam.kryptobot.feature.wallet.repository.WalletRepository

class SwapperScreenModel(
    private val swapperRepository: SwapperRepository,
    private val scannerRepository: ScannerRepository,
    private val walletRepository: WalletRepository,
) : ScreenModel, ScannerRepository by scannerRepository, SwapperRepository by swapperRepository {

    val uiState: StateFlow<SwapperScreenUiState> = combineStates(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        currentQuotes,
        latestDexPairs,
        currentSwapInstructions,
        currentSwapResponse,
        ::mapSwapScreenUiState
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

    /*
        TODO: Actual function that takes parameters for getting quote
     */
    private fun getQuote(dexPair: DexPairSwapUiModel) {
        screenModelScope.launch {
            swapperRepository.getQuote(
                inputAddress = dexPair.quoteToken?.address ?: "",
                outputAddress = dexPair.baseToken?.address ?: "",
                amount = 0.001,
            )
        }
    }
}