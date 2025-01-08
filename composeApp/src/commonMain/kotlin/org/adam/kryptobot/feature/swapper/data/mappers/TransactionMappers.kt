package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.data.model.toUi
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.util.calculatePercentChange
import org.adam.kryptobot.util.formatToDecimalString
import java.math.BigDecimal

fun Transaction.toTransactionUiModel(livePrice: BigDecimal): TransactionUiModel {
    val percentChange = calculatePercentChange(this.initialDexPriceSol, livePrice)

    return TransactionUiModel(
        amount = this.amount.formatToDecimalString(),
        swapMode = this.swapMode,
        inToken = this.inToken.toUi(),
        outToken = this.outToken.toUi(),
        transactionStep = this.transactionStep,
        slippageBps = this.quoteDto?.slippageBps ?: 0,
        fees = this.fee.formatToDecimalString(),
        initialPriceSol = this.initialDexPriceSol.formatToDecimalString(),
        percentChange = percentChange.toString(),
    )
}

