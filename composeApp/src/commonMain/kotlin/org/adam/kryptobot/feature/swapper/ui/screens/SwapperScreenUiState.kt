package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.data.mappers.toDexPairSwapUiModel
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.data.model.QuoteParamsConfig
import org.adam.kryptobot.feature.swapper.data.model.TransactionStep
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

/*
    TODO values for the selected quotes that get mapped from checkbox to list i.e dexes/exclude dexes
 */
data class SwapperScreenUiState(
    val pair: List<DexPairSwapUiModel> = listOf(),
    val quoteParams: QuoteParamsConfig = QuoteParamsConfig(),
    val selectedPair: DexPairSwapUiModel? = null,
    val selectedTransactionSteps: List<TransactionStep> = listOf(),
    )

fun mapSwapScreenUiState(
    pair: List<DexPair> = listOf(),
    trackedTokenAddresses: Set<String>,
    selectedPair: DexPairSwapUiModel?,
    quoteConfig: QuoteParamsConfig,
    transactionSteps: Map<String, List<TransactionStep>>,
): SwapperScreenUiState {
    /*
        TODO: Filter by tracked addresses and then add associated steps to each pair
     */
    return SwapperScreenUiState(
        pair = pair.filter { trackedTokenAddresses.contains(it.baseToken?.address) }
            .map { it.toDexPairSwapUiModel() },
        quoteParams = quoteConfig,
        selectedPair = selectedPair,
        selectedTransactionSteps = transactionSteps[selectedPair?.baseToken?.address] ?: listOf()
    )
}