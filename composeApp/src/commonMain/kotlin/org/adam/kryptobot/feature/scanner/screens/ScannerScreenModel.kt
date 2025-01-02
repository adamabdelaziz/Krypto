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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase

class ScannerScreenModel(
    private val scannerRepository: ScannerRepository,
    private val monitorTokenAddresses: MonitorTokenAddressesUseCase,
) : ScreenModel, ScannerRepository by scannerRepository{

    val uiState: StateFlow<ScannerScreenUiState> = combine(
        latestDexPairs,
        ordersPaidForByTokenAddress
    ) { latestDexPairs, orders ->
        ScannerScreenUiState(
            latestDexPairs = latestDexPairs,
            currentPaymentStatus = orders,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScannerScreenUiState()
    )

    fun onEvent(event: ScannerScreenEvent) {
        when (event) {
            is ScannerScreenEvent.OnTokenAddressSelected -> {
                trackPair(event.pair)
            }

            is ScannerScreenEvent.OnTokenCategorySelected -> {
                monitorTokenAddresses(event.category)
            }

            ScannerScreenEvent.OnStopSelected -> {
                monitorTokenAddresses.stop()
            }
        }
    }
}