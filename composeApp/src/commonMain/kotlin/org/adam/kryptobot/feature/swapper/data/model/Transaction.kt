package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep

data class Transaction(
    val quoteRaw: String,
    val inputSymbol: String,
    val inputAddress: String,
    val outputAddress: String,
    val outputSymbol: String,
    val inputAmount: String,
    val outputAmount: String,
    val swapMode: SwapMode,
    val quoteDto: JupiterQuoteDto? = null,
    val swapResponse: JupiterSwapResponseDto?= null,
    val transactionSignature: String? = null,
    val status: Status = Status.PENDING,
    val transactionStep: TransactionStep = TransactionStep.QUOTE_MADE,
    val amount: Double,
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
