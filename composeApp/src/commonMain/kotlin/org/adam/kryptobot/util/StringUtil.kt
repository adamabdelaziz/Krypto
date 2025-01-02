package org.adam.kryptobot.util

import org.sol4k.Base58
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

fun String.titleCase(): String {
    return this.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}