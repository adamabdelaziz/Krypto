package org.adam.kryptobot.util

fun detectEncoding(input: String): EncodingType {
    if (input.matches(Regex("^[A-Za-z0-9+/=]+$")) && input.length % 4 == 0) {
        return EncodingType.BASE64
    }

    if (input.matches(Regex("^[0-9a-fA-F]+$")) && input.length % 2 == 0) {
        return EncodingType.HEXADECIMAL
    }

    if (input.matches(Regex("^[1-9A-HJ-NP-Za-km-z]+$"))) {
        return EncodingType.BASE58
    }

    if (input.all { it in ' '..'~' }) {
        return EncodingType.PLAIN_TEXT
    }

    return EncodingType.UNKNOWN
}
enum class EncodingType {
    BASE64,
    HEXADECIMAL,
    BASE58,
    PLAIN_TEXT,
    UNKNOWN
}