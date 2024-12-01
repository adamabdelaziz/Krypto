package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val url: String,
    val chainId: String,
    val tokenAddress: String,
    val icon: String? = null,
    val header: String? = null,
    val openGraph:String? = null,
    val description: String? = null,
    val links: List<LinkDto>? = null,
)