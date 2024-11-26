package org.adam.kryptobot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform