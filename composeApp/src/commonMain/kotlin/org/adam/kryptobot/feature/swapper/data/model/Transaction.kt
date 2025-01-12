package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.swapper.data.dto.JupiterQuoteDto
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapResponseDto
import org.adam.kryptobot.feature.swapper.enum.Status
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import org.adam.kryptobot.feature.swapper.enum.TransactionStep
import java.math.BigDecimal

data class Transaction(
    val quoteRaw: String, //Used as a key and for further API calls
    val amount: Double, //effectively the inToken.amount but its what is typed in when first making the quote
    val initialDexPriceSol: BigDecimal,
    val inToken: TransactionToken,
    val outToken: TransactionToken,
    val swapMode: SwapMode,
    val fee: BigDecimal = BigDecimal.ZERO,
    val quoteDto: JupiterQuoteDto? = null,
    val swapResponse: JupiterSwapResponseDto? = null,
    val transactionSignature: String? = null,
    val status: Status = Status.PENDING,
    val transactionStep: TransactionStep = TransactionStep.QUOTE_MADE,
) {
    /*
        TODO: See if this is useful. Seems like initialDexPriceSol is the price in terms of the token in SOL
            so that should be good enough for tracking for profit
     */
    val initialMonkasSol
        get() = determineInitialPriceSol()

    private fun determineInitialPriceSol(): BigDecimal {
        val inAmount = BigDecimal(quoteDto?.inAmount).movePointLeft(inToken.decimals)
        val outAmount = BigDecimal(quoteDto?.outAmount).movePointLeft(outToken.decimals)

        return when (swapMode) {
            SwapMode.ExactIn -> outAmount / inAmount
            SwapMode.ExactOut -> inAmount / outAmount
        }
    }
}
