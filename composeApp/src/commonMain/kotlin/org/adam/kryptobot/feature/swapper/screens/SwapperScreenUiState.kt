package org.adam.kryptobot.feature.swapper.screens

import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto

data class SwapperScreenUiState(
    val quote: String? = null,
    val pair: DexPair? = null,
)