package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BoostedTokenDto(
    val url: String,
    val chainId: String,
    val tokenAddress: String,
    val amount: Int? = null,
    val totalAmount: Int,
    val icon: String? = null,
    val header: String? = null,
    val description: String? = null,
    val links: List<LinkDto>? = null,
)