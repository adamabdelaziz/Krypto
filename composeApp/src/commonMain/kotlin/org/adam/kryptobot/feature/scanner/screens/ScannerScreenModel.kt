package org.adam.kryptobot.feature.scanner.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cancelAndNull
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.data.dto.Token
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository

class ScannerScreenModel(
    private val scannerRepository: ScannerRepository,
) : ScreenModel {

    private val _selectedTokenCategory: MutableStateFlow<TokenCategory> =
        MutableStateFlow(TokenCategory.LATEST)
    val selectedCategory: StateFlow<TokenCategory> = _selectedTokenCategory

    val uiState: StateFlow<ScannerScreenUiState> = combine(
        scannerRepository.latestTokens,
        scannerRepository.latestBoostedTokens,
        scannerRepository.latestDexPairs,
        _selectedTokenCategory,
    ) { latestTokens, latestBoostedTokens, latestDexPairs, selectedTokenCategory ->
        val pairs = latestDexPairs[selectedTokenCategory] ?: listOf()
        ScannerScreenUiState(
            latestTokens = latestTokens,
            latestBoostedTokens = latestBoostedTokens,
            latestDexPairs = pairs,
            selectedTokenCategory = selectedTokenCategory
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScannerScreenUiState()
    )

    private var tokenScanJob: Job? = null
    private var boostedTokenScanJob: Job? = null
    private var mostBoostedTokenScanJob: Job? = null
    private var monitorTokenAddressJob: Job? = null

    init {
        scanBoostedTokens()
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
                monitorAllTokenAddress()
            }

            is ScannerScreenEvent.OnTokenCategorySelected -> {
                switchCategory(event.category)
            }

            ScannerScreenEvent.OnStopSelected -> {
                stopAllScanning()
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

    private fun scanMostBoostedTokens() {
        mostBoostedTokenScanJob?.cancelAndNull()
        mostBoostedTokenScanJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getMostActiveBoostedTokens()
                delay(SCAN_DELAY)
            }
        }
        mostBoostedTokenScanJob?.start()
    }

    private fun monitorTokenAddress(chainId: String, tokenAddress: String) {
        Logger.d("monitorTokenAddress monkas $chainId $tokenAddress called")
        monitorTokenAddressJob?.cancelAndNull()
        monitorTokenAddressJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getDexPairsByChainAndAddress(chainId, tokenAddress)
                delay(SCAN_DELAY)
            }
        }
    }

    private fun monitorAllTokenAddress() {
        monitorTokenAddressJob?.cancelAndNull()
        monitorTokenAddressJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getDexPairsByAddressList(_selectedTokenCategory.value)
                delay(SCAN_DELAY)
            }
        }
    }

    private fun switchCategory(category: TokenCategory) {
        _selectedTokenCategory.value = category
        stopAllScanning()
        when (category) {
            TokenCategory.LATEST_BOOSTED -> scanBoostedTokens()
            TokenCategory.MOST_ACTIVE_BOOSTED -> scanMostBoostedTokens()
            TokenCategory.LATEST -> scanTokens()
        }
        monitorAllTokenAddress()
    }

    private fun stopAllScanning() {
        monitorTokenAddressJob?.cancelAndNull()
        tokenScanJob?.cancelAndNull()
        boostedTokenScanJob?.cancelAndNull()
    }

    companion object {
        private const val SCAN_DELAY = 15000L
    }
}