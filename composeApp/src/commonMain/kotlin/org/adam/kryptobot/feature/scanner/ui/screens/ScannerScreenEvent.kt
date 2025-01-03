package org.adam.kryptobot.feature.scanner.ui.screens

import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel

sealed class ScannerScreenEvent {
    data object OnStopSelected: ScannerScreenEvent()

    class OnTokenAddressSelected(val pair: DexPairScanUiModel): ScannerScreenEvent()
    class OnTokenCategorySelected(val category: TokenCategory?): ScannerScreenEvent()
    class OnChainFilterToggled(val chain: Chain?): ScannerScreenEvent()
    class OnDexFilterToggled(val dex: Dex?): ScannerScreenEvent()
}