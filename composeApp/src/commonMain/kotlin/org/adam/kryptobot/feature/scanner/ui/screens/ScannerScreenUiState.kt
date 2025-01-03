package org.adam.kryptobot.feature.scanner.ui.screens

import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.mappers.toDexPairScanUiModel
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.util.filterIf

data class ScannerScreenUiState(
    val latestDexPairs: List<DexPairScanUiModel> = emptyList(),
    val currentPaymentStatus: List<PaymentStatusDto> = emptyList(),
    val selectedChainFilters: Set<Chain> = emptySet(),
    val selectedDexFilters: Set<Dex> = emptySet(),
    val isScanRunning: Boolean = false,
    val selectedCategory: TokenCategory? = TokenCategory.MOST_ACTIVE_BOOSTED,
)

fun mapScannerState(
    dexPairs: List<DexPair>,
    orders: List<PaymentStatusDto>,
    chainFilter: Set<Chain>,
    dexFilter: Set<Dex>,
    isScanRunning: Boolean,
    selectedCategory: TokenCategory?,
    trackedSet: Set<String>,
): ScannerScreenUiState {
    val filteredList = dexPairs.filterIf(dexFilter.isNotEmpty()) { pair ->
        !dexFilter.any { dex -> dex.name.equals(pair.dexId, ignoreCase = true) }
    }.filterIf(chainFilter.isNotEmpty()) { pair ->
        !chainFilter.any { chain -> chain.name.equals(pair.chainId, ignoreCase = true) }
    }.map { pair ->
        pair.toDexPairScanUiModel(trackedSet.contains(pair.baseToken?.address))
    }

    return ScannerScreenUiState(
        latestDexPairs = filteredList,
        currentPaymentStatus = orders,
        selectedChainFilters = chainFilter,
        selectedDexFilters = dexFilter,
        isScanRunning = isScanRunning,
        selectedCategory = selectedCategory,
    )
}