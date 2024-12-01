package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.DexPairDto
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto

data class ScannerScreenUiState(
    val latestTokens: List<TokenDto> = emptyList(),
    val latestBoostedTokens: List<BoostedTokenDto> = emptyList(),
    val latestDexPairs: Map<String,DexPairDto> = mapOf(),
)