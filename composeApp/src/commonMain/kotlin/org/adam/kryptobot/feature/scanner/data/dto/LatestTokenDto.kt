package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable
import org.adam.kryptobot.feature.scanner.data.model.Token

@Serializable
data class LatestTokenDto(
    val url: String,
    val chainId: String,
    val tokenAddress: String,
    val icon: String? = null,
    val header: String? = null,
    val openGraph: String? = null,
    val description: String? = null,
    val links: List<LinkDto>? = null,
)

fun LatestTokenDto.toToken(): Token = Token(
    url = this.url,
    chainId = this.chainId,
    tokenAddress = this.tokenAddress,
    icon = this.icon,
    header = this.header,
    link = this.links?.firstOrNull()?.url
)
