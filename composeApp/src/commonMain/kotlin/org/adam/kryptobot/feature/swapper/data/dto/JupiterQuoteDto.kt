package org.adam.kryptobot.feature.swapper.data.dto

import co.touchlab.kermit.Logger
import kotlinx.serialization.Serializable
import java.math.BigDecimal

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
    val contextSlot: Double,
    //val scoreReport: String? = null,
    val timeTaken: Double
)

@Serializable
data class PlatformFeeDto(
    val amount: String,
    val feeBps: Double
)

@Serializable
data class RoutePlanDto(
    val swapInfo: SwapInfoDto,
    val percent: Double
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

fun JupiterQuoteDto.getTotalFees(): BigDecimal {
    val routeFees = this.routePlan
        .map { it.swapInfo.feeAmount.toBigDecimal() }
        .fold(BigDecimal.ZERO) { acc, fee -> acc + fee }
    val platformFee = this.platformFee?.amount?.toBigDecimal() ?: BigDecimal.ZERO

    return routeFees + platformFee
}