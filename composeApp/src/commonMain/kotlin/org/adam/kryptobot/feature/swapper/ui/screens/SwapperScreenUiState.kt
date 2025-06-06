package org.adam.kryptobot.feature.swapper.ui.screens

import org.adam.kryptobot.feature.scanner.data.mappers.toDexPairSwapUiModel
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.Chain
import org.adam.kryptobot.feature.swapper.data.mappers.toTransactionUiModel
import org.adam.kryptobot.feature.swapper.data.model.QuoteParamsConfig
import org.adam.kryptobot.feature.swapper.data.model.SwapStrategy
import org.adam.kryptobot.feature.swapper.data.model.TrackedTransaction
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.util.filterIf
import java.math.BigDecimal

data class SwapperScreenUiState(
    val pair: List<DexPairSwapUiModel> = listOf(),
    val quoteParams: QuoteParamsConfig = QuoteParamsConfig(),
    val selectedPair: DexPairSwapUiModel? = null,
    val selectedTransactions: List<TransactionUiModel> = listOf(),
    val selectedStrategy: SwapStrategy? = null,
)

fun mapSwapScreenUiState(
    pair: List<DexPair> = listOf(),
    trackedTokenAddresses: Set<String>,
    selectedPair: DexPairSwapUiModel?,
    swapStrategies: List<SwapStrategy>,
    quoteConfig: QuoteParamsConfig,
    transactions: List<Transaction>,
    trackedTransactions: List<TrackedTransaction>,
): SwapperScreenUiState {
    val livePrice = selectedPair?.priceSol?.let { BigDecimal(it) } ?: BigDecimal.ZERO
    val trackedTransaction = trackedTransactions.firstOrNull { it.transaction.outToken.address == selectedPair?.baseToken?.address }

    return SwapperScreenUiState(
        pair = pair.filter { trackedTokenAddresses.contains(it.baseToken?.address) }
            .filterIf(quoteConfig.excludeDexes.isNotEmpty()) { pair ->
                !quoteConfig.excludeDexes.any { dex -> dex.name.equals(pair.dexId, ignoreCase = true) }
            }.filter { it.chainId.equals(Chain.Solana.name, ignoreCase = true) }.map { it.toDexPairSwapUiModel() },
        quoteParams = quoteConfig,
        selectedPair = selectedPair,
        selectedTransactions = transactions.filter { it.inToken.address == selectedPair?.baseToken?.address || it.outToken.address == selectedPair?.baseToken?.address }
            .map { it.toTransactionUiModel(livePrice, trackedTransaction) },
        selectedStrategy = swapStrategies.firstOrNull { it.key == selectedPair?.baseToken?.address },
    )
}