package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.enum.TokenCategory

sealed class ScannerScreenEvent {
    data object OnTokenViewSelected : ScannerScreenEvent()
    data object OnBoostedTokenViewSelected : ScannerScreenEvent()
    data object OnStopSelected: ScannerScreenEvent()

    class OnTokenAddressSelected(val chainId: String, val tokenAddress: String): ScannerScreenEvent()
    class OnTokenCategorySelected(val category: TokenCategory): ScannerScreenEvent()
}