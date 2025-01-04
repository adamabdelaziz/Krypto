package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.enum.Status

data class TransactionStep(
    val quote: String,
    val swapResponse: JupiterSwapResponseDto?= null,
    val transactionSignature: String? = null,
    val status: Status = Status.PENDING,
    val amount: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransactionStep) return false
        return quote == other.quote
    }

    override fun hashCode(): Int {
        return quote.hashCode()
    }
}
