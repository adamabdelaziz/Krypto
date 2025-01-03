package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.data.mappers.toDexPairSwapUiModel
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

data class SwapperScreenUiState(
    val quote: String? = null,
    val pair: List<DexPairSwapUiModel> = listOf(),
    val swapInstructions: JupiterSwapInstructionsDto? = null,
    val swapResponse: JupiterSwapResponseDto? = null,
)

fun mapSwapScreenUiState(
    quote: String? = null,
    pair: List<DexPair> = listOf(),
    swapInstructions: JupiterSwapInstructionsDto? = null,
    swapResponse: JupiterSwapResponseDto? = null,
): SwapperScreenUiState {
    return SwapperScreenUiState(
        quote = quote,
        pair = pair.map { it.toDexPairSwapUiModel() },
        swapInstructions = swapInstructions,
        swapResponse = swapResponse,
    )
}