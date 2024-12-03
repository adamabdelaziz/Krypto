package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.LatestTokenDto
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.TokenCategory

data class ScannerScreenUiState(
    val latestTokens: List<LatestTokenDto> = emptyList(),
    val latestBoostedTokens: List<BoostedTokenDto> = emptyList(),
    val latestDexPairs: List<DexPair> = emptyList(),
    val currentPaymentStatus: List<PaymentStatusDto> = emptyList(),
    val selectedTokenCategory: TokenCategory = TokenCategory.LATEST,
)