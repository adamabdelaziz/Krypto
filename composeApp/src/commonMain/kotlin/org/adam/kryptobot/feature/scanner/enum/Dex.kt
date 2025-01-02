package org.adam.kryptobot.feature.scanner.enum

enum class Dex(private val displayName: String) {
    RAYDIUM("raydium"),
    METEORA("meteora"),
    ORCA("orca"),
    UNISWAP("uniswap");

    override fun toString(): String {
        return displayName
    }
}