package org.adam.kryptobot.feature.scanner.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cancelAndNull
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository

class ScannerScreenModel(
    private val scannerRepository: ScannerRepository,
) : ScreenModel {

    val uiState: StateFlow<ScannerScreenUiState> = combine(
        scannerRepository.latestTokens,
        scannerRepository.latestBoostedTokens,
        scannerRepository.latestDexPairs,
    ) { latestTokens, latestBoostedTokens, latestDexPairs ->
        ScannerScreenUiState(
            latestTokens = latestTokens,
            latestBoostedTokens = latestBoostedTokens,
            latestDexPairs = latestDexPairs
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScannerScreenUiState()
    )

    private var tokenScanJob: Job? = null
    private var boostedTokenScanJob: Job? = null
    private var monitorTokenAddressJob: Job? = null

    init {
        scanTokens()
    }

    fun onEvent(event: ScannerScreenEvent) {
        when (event) {
            ScannerScreenEvent.OnBoostedTokenViewSelected -> {
                tokenScanJob?.cancelAndNull()
                scanBoostedTokens()
            }

            ScannerScreenEvent.OnTokenViewSelected -> {
                boostedTokenScanJob?.cancelAndNull()
                scanTokens()
            }

            is ScannerScreenEvent.OnTokenAddressSelected -> {
                monitorTokenAddress(event.chainId, event.tokenAddress)
            }
        }
    }

    private fun scanTokens() {
        Logger.d("scan tokens called")
        tokenScanJob?.cancelAndNull()
        tokenScanJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getLatestTokens()
                delay(SCAN_DELAY)
            }
        }
        tokenScanJob?.start()
    }

    private fun scanBoostedTokens() {
        boostedTokenScanJob?.cancelAndNull()
        boostedTokenScanJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getLatestBoostedTokens()
                delay(SCAN_DELAY)
            }
        }
        boostedTokenScanJob?.start()
    }

    private fun monitorTokenAddress(chainId:String, tokenAddress: String) {
        Logger.d("monitorTokenAddress monkas $tokenAddress called")
        monitorTokenAddressJob?.cancelAndNull()
        monitorTokenAddressJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getDexPairsByTokenAddress(chainId, tokenAddress)
                delay(SCAN_DELAY)
            }
        }
    }


    companion object {
        private const val SCAN_DELAY = 15000L
    }
}