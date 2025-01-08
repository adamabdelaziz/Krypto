package org.adam.kryptobot.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun Double.formatToDollarString(): String {
    return "$" + "%,.2f".format(this)
}

fun BigDecimal.formatToDollarString(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(this)
}

/*
    For 0.000001 instead of scientific notation
 */
fun Double.formatToDecimalString(): String {
    return BigDecimal(this.toString()).stripTrailingZeros().toPlainString()
}

fun BigDecimal.formatToDecimalString(): String {
    return this.stripTrailingZeros().toPlainString()
}

fun calculatePercentChange(initialPrice: BigDecimal, livePrice: BigDecimal): BigDecimal {
    return if (initialPrice > BigDecimal.ZERO) {
        ((livePrice - initialPrice) / initialPrice * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
}