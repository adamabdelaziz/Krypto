package org.adam.kryptobot.feature.swapper.ui.model

import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep

data class TransactionUiModel (
    val amount: String,
    val swapMode: SwapMode,
    val inSymbol: String,
    val outSymbol: String,
    val inAmount: String,
    val outAmount: String,
    val transactionStep: TransactionStep,
    val feesLamport:String,
    val feesSol:String,
    val slippageBps: Int,
)