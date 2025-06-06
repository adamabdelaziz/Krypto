package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable
import org.adam.kryptobot.feature.scanner.data.model.Token

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

fun BoostedTokenDto.toToken(): Token =
    Token(
        url = url,
        chainId = chainId,
        tokenAddress = tokenAddress,
        amount = amount,
        totalAmount = totalAmount,
        icon = icon,
        header = header,
        link = links?.firstOrNull()?.url
    )