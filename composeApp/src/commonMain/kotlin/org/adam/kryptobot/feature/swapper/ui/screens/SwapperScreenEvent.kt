package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.ui.model.DexPairUiModel

sealed class SwapperScreenEvent {
    data object OnGenerateDebugWalletClicked: SwapperScreenEvent()
    data class OnDexPairClicked(val dexPair: DexPairUiModel) : SwapperScreenEvent()
    data object OnGenerateSwapInstructionsClicked: SwapperScreenEvent()
    data object OnPerformSwapTransactionClicked: SwapperScreenEvent()
}