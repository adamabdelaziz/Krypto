package org.adam.kryptobot.feature.scanner.screens

import org.adam.kryptobot.feature.scanner.data.dto.BoostedTokenDto
import org.adam.kryptobot.feature.scanner.data.dto.Pair
import org.adam.kryptobot.feature.scanner.data.dto.PaymentStatusDto
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto
import org.adam.kryptobot.feature.scanner.enum.TokenCategory

data class ScannerScreenUiState(
    val latestTokens: List<TokenDto> = emptyList(),
    val latestBoostedTokens: List<BoostedTokenDto> = emptyList(),
    val latestDexPairs: List<Pair> = emptyList(),
    val currentPaymentStatus: List<PaymentStatusDto> = emptyList(),
    val selectedTokenCategory: TokenCategory = TokenCategory.LATEST,
)