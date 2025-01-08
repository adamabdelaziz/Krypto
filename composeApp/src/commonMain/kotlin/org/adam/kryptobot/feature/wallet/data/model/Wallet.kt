package org.adam.kryptobot.feature.wallet.data.model

data class Wallet(
    val publicKey: String = "",
    val privateAddress: String = "", // Likely the really long one used for ByteArray
    val balance: String = "", // Lamport value
    val tokenBalance: List<Pair<String, Double>> = listOf(),
) {
    val tokenList: List<String>
        get() = tokenBalance.map { it.first }
}