package org.adam.kryptobot.util

import org.sol4k.Base58
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun String.decodeBase58(): ByteArray {
    return Base58.decode(this)
}

fun ByteArray.encodeBase58(): String {
    return Base58.encode(this)
}