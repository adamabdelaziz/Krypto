package org.adam.kryptobot.feature.swapper.data.model

data class Wallet(
    val publicKey: String = "",
    val privateAddress: String = "", //Likely the really long one used for ByteArray
    val balance: String = "",
)