package org.adam.kryptobot.feature.scanner.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Dex(private val displayName: String) {
    @SerialName("raydium")
    Raydium("raydium"),

    @SerialName("meteora")
    Meteora("meteora"),

    @SerialName("orca")
    Orca("orca"),

    @SerialName("uniswap")
    Uniswap("uniswap");

    override fun toString(): String {
        return displayName
    }
}