package org.adam.kryptobot.feature.scanner.screens

sealed class ScannerScreenEvent {
    data object OnTokenViewSelected : ScannerScreenEvent()
    data object OnBoostedTokenViewSelected : ScannerScreenEvent()
    class OnTokenAddressSelected(val chainId: String, val tokenAddress: String): ScannerScreenEvent()
}