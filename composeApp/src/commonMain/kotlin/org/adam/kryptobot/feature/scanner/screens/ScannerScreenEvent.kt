package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.Pair
import org.adam.kryptobot.feature.scanner.enum.TokenCategory

sealed class ScannerScreenEvent {
    data object OnTokenViewSelected : ScannerScreenEvent()
    data object OnBoostedTokenViewSelected : ScannerScreenEvent()
    data object OnStopSelected: ScannerScreenEvent()

    class OnTokenAddressSelected(val pair: Pair): ScannerScreenEvent()
    class OnTokenCategorySelected(val category: TokenCategory): ScannerScreenEvent()
}