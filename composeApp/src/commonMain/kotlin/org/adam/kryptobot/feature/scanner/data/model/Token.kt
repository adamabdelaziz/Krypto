package org.adam.kryptobot.feature.scanner.data.model

data class Token(
    val url: String,
    val chainId: String,
    val tokenAddress: String,
    val symbol: String? = null,
    val icon: String? = null,
    val header: String? = null,
    val amount: Int? = null,
    val totalAmount: Int? = null,
    val link: String? = null,
)
