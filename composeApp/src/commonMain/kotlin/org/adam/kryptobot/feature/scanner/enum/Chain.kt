package org.adam.kryptobot.feature.scanner.enum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Chain(private val displayName: String) {
    @SerialName("solana")
    Solana("solana"),

    @SerialName("ethereum")
    Ethereum("ethereum");

    override fun toString(): String {
        return displayName
    }
}