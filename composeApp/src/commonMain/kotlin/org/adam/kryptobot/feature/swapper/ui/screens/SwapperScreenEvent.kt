package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

sealed class SwapperScreenEvent {
    data object OnGenerateDebugWalletClicked: SwapperScreenEvent()
    data class OnDexPairClicked(val dexPair: DexPairSwapUiModel) : SwapperScreenEvent()
    data object OnGenerateSwapInstructionsClicked: SwapperScreenEvent()
    data object OnPerformSwapTransactionClicked: SwapperScreenEvent()
}