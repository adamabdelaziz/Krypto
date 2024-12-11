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

//TODO encrypt/save wallet info and move to repo
const val MAIN_WALLET_PUBLIC_KEY = "67fVSRo7LbY14JVZf9Zq6Y1YBoxbTXCYr4n18AKUsNS7"
const val MAIN_WALLET_PRIVATE_KEY = "5iafqcbi7uWHKmUNXHcTA8KRnuywKkfap8vv8r5egCA9rmt8aQnzEbCJihXDsG3Q1xEngyd46P91cYat1Ab35Vc7"
const val DEV_WALLET_PRIVATE_KEY =
    "5MfbR9MuTYeNxtiYoAnqsuhchWTtQHNmM7uagXMqNgzVbmLdBHpu44Fj4pDPfYDdH43uB58WXaZX5B4XJASKBxwR"