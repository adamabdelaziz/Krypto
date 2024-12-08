package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JupiterSwapParametersDto(
    val wrapAndUnwrapSol: Boolean,
    val useSharedAccounts: Boolean,
    val feeAccount: String,
    val trackingAccount: String,
    val computeUnitPriceMicroLamports: Long,
    val prioritizationFeeLamports: Long,
    val asLegacyTransaction: Boolean,
    val useTokenLedger: Boolean,
    val destinationTokenAccount: String,
    val dynamicComputeUnitLimit: Boolean,
    val skipUserAccountsRpcCalls: Boolean,
    val dynamicSlippage: DynamicSlippage
)

@Serializable
data class DynamicSlippage(
    val minBps: Int,
    val maxBps: Int
)

/*
    Quote response is from quote endpoint,
 */
@Serializable
data class JupiterSwapWrapperDto(
    val userPublicKey: String,
    val swapTransactionConfig: JupiterSwapParametersDto,
    val quoteResponse: JupiterQuoteDto,
)