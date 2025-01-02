package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.LatestTokenDto
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.enum.TokenCategory
import org.adam.kryptobot.util.filterIf

data class ScannerScreenUiState(
    val latestDexPairs: List<DexPair> = emptyList(),
    val currentPaymentStatus: List<PaymentStatusDto> = emptyList(),
    val selectedChainFilters: Set<Chain> = emptySet(),
    val selectedDexFilters: Set<Dex> = emptySet(),
    val isScanRunning: Boolean = false,
    val selectedCategory: TokenCategory? = TokenCategory.MOST_ACTIVE_BOOSTED,
)

fun mapState(
    dexPairs: List<DexPair>,
    orders: List<PaymentStatusDto>,
    chainFilter: Set<Chain>,
    dexFilter: Set<Dex>,
    isScanRunning: Boolean,
    selectedCategory: TokenCategory?,
): ScannerScreenUiState {
    val filteredList = dexPairs.filterIf(dexFilter.isNotEmpty()) { pair ->
        !dexFilter.any { dex -> dex.name.equals(pair.dexId, ignoreCase = true) }
    }.filterIf(chainFilter.isNotEmpty()) { pair ->
        !chainFilter.any { chain -> chain.name.equals(pair.chainId, ignoreCase = true) }
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