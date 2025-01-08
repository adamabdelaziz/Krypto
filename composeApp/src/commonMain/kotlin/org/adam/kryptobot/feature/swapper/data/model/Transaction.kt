package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep
import java.math.BigDecimal

data class Transaction(
    val quoteRaw: String,
    val amount: Double, //effectively the inToken.amount but its what is typed in when first making the quote
    val initialPriceSol: BigDecimal,
    val inToken: TransactionToken,
    val outToken: TransactionToken,
    val swapMode: SwapMode,
    val fee: String? = null,
    val quoteDto: JupiterQuoteDto? = null,
    val swapResponse: JupiterSwapResponseDto?= null,
    val transactionSignature: String? = null,
    val status: Status = Status.PENDING,
    val transactionStep: TransactionStep = TransactionStep.QUOTE_MADE,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false
        return quoteRaw == other.quoteRaw
    }

    override fun hashCode(): Int {
        return quoteRaw.hashCode()
    }
}
