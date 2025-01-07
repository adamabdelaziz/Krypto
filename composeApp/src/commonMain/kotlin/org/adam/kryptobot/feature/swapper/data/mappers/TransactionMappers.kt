package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.util.formatToDecimalString

fun Transaction.toTransactionUiModel(): TransactionUiModel {
    return TransactionUiModel(
        amount = this.amount.formatToDecimalString(),
        swapMode = this.swapMode,
        inputSymbol = this.inputSymbol,
        outputSymbol = this.outputSymbol,
        transactionStep = this.transactionStep,
        platformFeeAmount = this.quoteDto?.platformFee?.amount ?: "",
        platformFeeBps = this.quoteDto?.platformFee?.feeBps ?: 0.0,
        slippageBps = this.quoteDto?.slippageBps ?: 0,
        inputAmount = this.inputAmount,
        outputAmount = this.outputAmount,
    )
}

