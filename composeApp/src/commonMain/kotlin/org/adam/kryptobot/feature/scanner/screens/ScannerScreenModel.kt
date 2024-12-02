package org.adam.kryptobot.feature.scanner.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.adam.kryptobot.util.cancelAndNull
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
        scannerRepository.ordersPaidForByTokenAddress
    ) { latestTokens, latestBoostedTokens, latestDexPairs, selectedTokenCategory, orders ->
        val pairs = latestDexPairs[selectedTokenCategory] ?: listOf()
        ScannerScreenUiState(
            latestTokens = latestTokens,
            latestBoostedTokens = latestBoostedTokens,
            latestDexPairs = pairs,
            selectedTokenCategory = selectedTokenCategory,
            currentPaymentStatus = orders,
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
    private var monitorTokenAddressJobTwo: Job? = null
    private var monitorOrderJob: Job? = null

    init {
        // scanBoostedTokens()
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
                val chainId = event.pair.chainId ?: ""
                val tokenAddress = event.pair.baseToken?.address ?: ""
                val pairAddress = event.pair.pairAddress ?: ""
                monitorOrders(chainId, tokenAddress)
                //monitorTokenAddress(chainId, pairAddress)
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
        monitorTokenAddressJobTwo?.cancelAndNull()
        monitorTokenAddressJobTwo = screenModelScope.launch {
            while (true) {
                scannerRepository.getDexPairsByChainAndAddress(chainId, tokenAddress)
                delay(SCAN_DELAY)
            }
        }
        monitorTokenAddressJobTwo?.start()
    }

    private fun monitorOrders(chainId: String, tokenAddress: String) {
        monitorOrderJob?.cancelAndNull()
        monitorOrderJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getOrdersPaidFor(chainId, tokenAddress)
                delay(SCAN_DELAY)
            }
        }
        monitorOrderJob?.start()
    }

    private fun monitorAllTokenAddress() {
        monitorTokenAddressJob?.cancelAndNull()
        monitorTokenAddressJob = screenModelScope.launch {
            while (true) {
                scannerRepository.getDexPairsByAddressList(_selectedTokenCategory.value)
                delay(SCAN_DELAY)
            }
        }
        monitorTokenAddressJob?.start()
    }

    private fun switchCategory(category: TokenCategory) {
        _selectedTokenCategory.value = category
        stopAllScanning()
        when (category) {
            TokenCategory.LATEST_BOOSTED -> {
                scanBoostedTokens()
                screenModelScope.launch {
                    scannerRepository.latestBoostedTokens
                        .filter { it.isNotEmpty() }
                        .first()
                        .let { monitorAllTokenAddress() }
                }
            }

            TokenCategory.MOST_ACTIVE_BOOSTED -> {
                scanMostBoostedTokens()
                screenModelScope.launch {
                    scannerRepository.mostActiveBoostedTokens
                        .filter { it.isNotEmpty() }
                        .first()
                        .let { monitorAllTokenAddress() }
                }
            }

            TokenCategory.LATEST -> {
                scanTokens()
                screenModelScope.launch {
                    scannerRepository.latestTokens
                        .filter { it.isNotEmpty() }
                        .first()
                        .let { monitorAllTokenAddress() }
                }
            }
        }
    }

    private fun stopAllScanning() {
        monitorTokenAddressJob?.cancelAndNull()
        tokenScanJob?.cancelAndNull()
        boostedTokenScanJob?.cancelAndNull()
        monitorTokenAddressJobTwo?.cancelAndNull()
        monitorOrderJob?.cancelAndNull()
    }

    companion object {
        private const val SCAN_DELAY = 15000L
    }
}