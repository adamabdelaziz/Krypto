package org.adam.kryptobot.feature.scanner.enum

enum class Chain(private val displayName: String) {
    SOLANA("solana"),
    ETHEREUM("ethereum");

    override fun toString(): String {
        return displayName
    }
}