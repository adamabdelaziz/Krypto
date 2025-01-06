package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel

fun Transaction.toTransactionUiModel(): TransactionUiModel {
    return TransactionUiModel(
        amount = this.amount,
        swapMode = this.swapMode,
        inputSymbol = this.inputSymbol,
        outputSymbol = this.outputSymbol,
        transactionStep = this.transactionStep,
        platformFeeAmount = this.quoteDto?.platformFee?.amount ?: "",
        platformFeeBps = this.quoteDto?.platformFee?.feeBps ?: 0.0,
        slippageBps = this.quoteDto?.slippageBps ?: 0,
    )
}

fun getSwapTokenAddresses(
    swapMode: SwapMode,
    baseTokenAddress: String,
    quoteTokenAddress: String
): Pair<String, String> {
    return when (swapMode) {
        SwapMode.ExactIn -> quoteTokenAddress to baseTokenAddress
        SwapMode.ExactOut -> baseTokenAddress to quoteTokenAddress
    }
}