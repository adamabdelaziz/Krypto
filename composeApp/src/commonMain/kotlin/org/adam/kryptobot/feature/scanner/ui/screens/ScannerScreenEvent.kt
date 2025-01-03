package org.adam.kryptobot.feature.scanner.ui.screens

import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.ui.model.DexPairUiModel

sealed class ScannerScreenEvent {
    data object OnStopSelected: ScannerScreenEvent()

    class OnTokenAddressSelected(val pair: DexPairUiModel): ScannerScreenEvent()
    class OnTokenCategorySelected(val category: TokenCategory?): ScannerScreenEvent()
    class OnChainFilterToggled(val chain: Chain?): ScannerScreenEvent()
    class OnDexFilterToggled(val dex: Dex?): ScannerScreenEvent()
}