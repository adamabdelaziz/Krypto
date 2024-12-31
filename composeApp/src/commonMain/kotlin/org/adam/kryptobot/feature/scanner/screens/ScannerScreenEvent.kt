package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.TokenCategory

sealed class ScannerScreenEvent {
    data object OnStopSelected: ScannerScreenEvent()

    class OnTokenAddressSelected(val pair: DexPair): ScannerScreenEvent()
    class OnTokenCategorySelected(val category: TokenCategory): ScannerScreenEvent()
}