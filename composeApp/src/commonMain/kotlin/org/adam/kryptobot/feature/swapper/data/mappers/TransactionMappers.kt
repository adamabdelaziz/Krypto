package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.dto.getTotalFees
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.util.formatToDecimalString
import org.adam.kryptobot.util.lamportsToSol
import org.adam.kryptobot.util.solToLamports
import java.math.BigDecimal

fun Transaction.toTransactionUiModel(): TransactionUiModel {
    val fees = this.quoteDto?.getTotalFees() ?: BigDecimal.ZERO

    return TransactionUiModel(
        amount = this.amount.formatToDecimalString(),
        swapMode = this.swapMode,
        inSymbol = this.inputSymbol,
        outSymbol = this.outputSymbol,
        transactionStep = this.transactionStep,
        slippageBps = this.quoteDto?.slippageBps ?: 0,
        inAmount = this.inputAmount,
        outAmount = this.outputAmount,
        feesLamport = fees.toPlainString(),
        feesSol = lamportsToSol(fees.toLong()).formatToDecimalString(),
    )
}

