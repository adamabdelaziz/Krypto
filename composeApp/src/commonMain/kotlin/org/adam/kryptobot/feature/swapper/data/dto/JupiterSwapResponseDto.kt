package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JupiterSwapResponseDto(
    val swapTransaction: String,
    val lastValidBlockHeight: Long,
    val prioritizationFeeLamports: Long? = null,
    val dynamicSlippageReport: DynamicSlippageReportDto? = null,
)

@Serializable
data class DynamicSlippageReportDto(
    val slippageBps: Int? = null,
    val otherAmount: Int? = null,
    val simulatedIncurredSlippageBps: Int? = null,
    val amplificationRatio: String? = null
)