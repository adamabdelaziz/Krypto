package org.adam.kryptobot.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun Double.formatToDollarString(): String {
    return "$" + "%,.2f".format(this)
}

fun BigDecimal.formatToDollarString(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(this)
}
