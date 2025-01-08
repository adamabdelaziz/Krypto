package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.util.formatToDecimalString
import java.math.BigDecimal

data class TransactionToken(
    val symbol: String,
    val address: String,
    val amount: BigDecimal,
    val amountLamports: Long,
    val decimals: Int,
)

data class TransactionTokenUi(
    val symbol: String,
    val amount: String,
    val amountLamports: String,
)

fun TransactionToken.toUi(): TransactionTokenUi {
    return TransactionTokenUi(
        symbol = this.symbol,
        amount = amount.formatToDecimalString(),
        amountLamports = this.amountLamports.toString()
    )
}