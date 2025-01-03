package org.adam.kryptobot.feature.scanner.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zhuinden.flowcombinetuplekt.combineStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase

class ScannerScreenModel(
    private val scannerRepository: ScannerRepository,
    private val monitorTokenAddresses: MonitorTokenAddressesUseCase,
) : ScreenModel, ScannerRepository by scannerRepository {

    private val _dexFilter: MutableStateFlow<Set<Dex>> = MutableStateFlow(setOf())
    private val _chainFilter: MutableStateFlow<Set<Chain>> = MutableStateFlow(setOf())
    private val _scanRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<ScannerScreenUiState> = combineStates(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        latestDexPairs,
        ordersPaidForByTokenAddress,
        _chainFilter,
        _dexFilter,
        _scanRunning,
        selectedTokenCategory,
        trackedTokenAddresses,
        ::mapScannerState
    )

    fun onEvent(event: ScannerScreenEvent) {
        when (event) {
            is ScannerScreenEvent.OnTokenAddressSelected -> {
                trackPair(event.pair.baseToken?.address)
            }

            is ScannerScreenEvent.OnTokenCategorySelected -> {
                monitorTokenAddresses(event.category)
                _scanRunning.value = true
            }

            ScannerScreenEvent.OnStopSelected -> {
                monitorTokenAddresses.stop()
                _scanRunning.value = false
            }

            is ScannerScreenEvent.OnChainFilterToggled -> {
                val filterList = _chainFilter.value.toMutableSet()
                event.chain?.let {
                    if (filterList.contains(it)) {
                        filterList.remove(it)
                    } else {
                        filterList.add(it)
                    }
                } ?: run {
                    filterList.clear()
                }
                _chainFilter.value = filterList.toSet()
            }

            is ScannerScreenEvent.OnDexFilterToggled -> {
                val filterList = _dexFilter.value.toMutableSet()
                event.dex?.let {
                    if (filterList.contains(it)) {
                        filterList.remove(it)
                    } else {
                        filterList.add(it)
                    }
                } ?: run {
                    filterList.clear()
                }
                _dexFilter.value = filterList.toSet()
            }
        }
    }
}