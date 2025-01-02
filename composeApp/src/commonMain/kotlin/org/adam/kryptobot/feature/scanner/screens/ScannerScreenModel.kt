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
import okhttp3.internal.filterList
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.repository.ScannerRepository
import org.adam.kryptobot.feature.scanner.usecase.MonitorTokenAddressesUseCase
import org.adam.kryptobot.util.filterIf

class ScannerScreenModel(
    private val scannerRepository: ScannerRepository,
    private val monitorTokenAddresses: MonitorTokenAddressesUseCase,
) : ScreenModel, ScannerRepository by scannerRepository {

    private val _dexFilter: MutableStateFlow<Set<Dex>> = MutableStateFlow(setOf())
    private val _chainFilter: MutableStateFlow<Set<Chain>> = MutableStateFlow(setOf())

    val uiState: StateFlow<ScannerScreenUiState> = combine(
        latestDexPairs,
        ordersPaidForByTokenAddress,
        _dexFilter,
        _chainFilter,
    ) { latestDexPairs, orders, dexFilter, chainFilter ->
        val filteredList = latestDexPairs.filterIf(dexFilter.isNotEmpty()) { pair ->
            !dexFilter.any { dex -> dex.name.equals(pair.dexId, ignoreCase = true) }
        }.filterIf(chainFilter.isNotEmpty()) { pair ->
            !chainFilter.any { chain -> chain.name.equals(pair.chainId, ignoreCase = true) }
        }
        Logger.d("Chain list is $chainFilter")
        Logger.d("Dex list is $dexFilter")
        ScannerScreenUiState(
            latestDexPairs = filteredList,
            currentPaymentStatus = orders,
            selectedChainFilters = chainFilter,
            selectedDexFilters = dexFilter,
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