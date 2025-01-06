package org.adam.kryptobot.feature.swapper.ui.model

import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep

data class TransactionUiModel (
    val amount: Double,
    val swapMode: SwapMode,
    val inputSymbol: String,
    val outputSymbol: String,
    val transactionStep: TransactionStep,
    val platformFeeAmount: String,
    val platformFeeBps: Double,
    val slippageBps: Int,
)