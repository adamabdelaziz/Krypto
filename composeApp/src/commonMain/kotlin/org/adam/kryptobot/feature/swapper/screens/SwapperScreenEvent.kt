package org.adam.kryptobot.feature.swapper.screens

import org.adam.kryptobot.feature.scanner.data.model.DexPair

sealed class SwapperScreenEvent {
    data object OnGenerateDebugWalletClicked: SwapperScreenEvent()
    data class OnDexPairClicked(val dexPair: DexPair) : SwapperScreenEvent()
}