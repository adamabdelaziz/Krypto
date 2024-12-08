package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JupiterQuoteDto(
    val inputMint: String,
    val inAmount: String,
    val outputMint: String,
    val outAmount: String,
    val otherAmountThreshold: String,
    val swapMode: String, // Can be "ExactIn" or "ExactOut"
    val slippageBps: Int,
    val platformFee: PlatformFeeDto? = null,
    val priceImpactPct: String,
    val routePlan: List<RoutePlanDto>,
    val contextSlot: Long,
    val timeTaken: Long
)

@Serializable
data class PlatformFeeDto(
    val amount: String,
    val feeBps: Int
)

@Serializable
data class RoutePlanDto(
    val swapInfo: SwapInfoDto,
    val percent: Int
)

@Serializable
data class SwapInfoDto(
    val ammKey: String,
    val label: String? = null,
    val inputMint: String,
    val outputMint: String,
    val inAmount: String,
    val outAmount: String,
    val feeAmount: String,
    val feeMint: String
)