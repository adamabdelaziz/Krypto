package org.adam.kryptobot.util

import org.sol4k.Base58
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.decodeBase58(): ByteArray {
    return Base58.decode(this)
}

fun ByteArray.encodeBase58(): String {
    return Base58.encode(this)
}

@OptIn(ExperimentalEncodingApi::class)
fun base64ToBase58(base64String: String): String {
    val decodedBytes = Base64.decode(base64String)
    return Base58.encode(decodedBytes)
}

@OptIn(ExperimentalEncodingApi::class)
fun base64ToBase58ByteArray(base64String: String): ByteArray {
    val decodedBytes = Base64.decode(base64String)
    return Base58.decode(Base58.encode(decodedBytes))
}

//TODO encrypt/save wallet info and move to repo
const val MAIN_WALLET_PUBLIC_KEY = "67fVSRo7LbY14JVZf9Zq6Y1YBoxbTXCYr4n18AKUsNS7"
const val MAIN_WALLET_PRIVATE_KEY = "5iafqcbi7uWHKmUNXHcTA8KRnuywKkfap8vv8r5egCA9rmt8aQnzEbCJihXDsG3Q1xEngyd46P91cYat1Ab35Vc7"

//my phantom one
const val SECOND_WALLET_PUBLIC_KEY = "HjqMEvV87gkAdm8kPqUhDpeaKNVuBDDZjQwFWc9qnHbu"
const val SECOND_WALLET_PRIVATE_KEY = "HhqKF5Na9QY2dTLauMTXBbqxHyWNswcUDSf4cWF8SnupbUA4Aqc4qdz3GzLP7agVnup5VWFBkKAuqFWbZAqPQrB"

const val DEV_WALLET_PRIVATE_KEY =
    "5MfbR9MuTYeNxtiYoAnqsuhchWTtQHNmM7uagXMqNgzVbmLdBHpu44Fj4pDPfYDdH43uB58WXaZX5B4XJASKBxwR"