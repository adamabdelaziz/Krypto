package org.adam.kryptobot.feature.swapper.screens

import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto

data class SwapperScreenUiState(
    val quote: String? = null,
    val pair: DexPair? = null,
    val swapInstructions: JupiterSwapInstructionsDto? = null,
    val swapResponse: JupiterSwapResponseDto? = null,
)