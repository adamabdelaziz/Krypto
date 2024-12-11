package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable


/*
    Quote response is from quote endpoint,
 */
@Serializable
data class JupiterSwapWrapperDto(
    val userPublicKey: String,
    val wrapAndUnwrapSol: Boolean,
    val useSharedAccounts: Boolean,
    val feeAccount: String, //Fee token account, it can be either the input mint or the output mint for ExactIn and only the input mint for ExactOut
    val trackingAccount: String?,
    val computeUnitPriceMicroLamports: Long?,
    val prioritizationFeeLamports: Long?,
    val asLegacyTransaction: Boolean,
    val useTokenLedger: Boolean,
    val destinationTokenAccount: String,
    val dynamicComputeUnitLimit: Boolean,
    val skipUserAccountsRpcCalls: Boolean,
    val dynamicSlippage: DynamicSlippage,
    val quoteResponse: JupiterQuoteDto,
)

@Serializable
data class JupiterSwapWrapDto(
    val userPublicKey: String,
    val quoteResponse: JupiterQuoteDto,
)
/*
    The user max slippage, note that jup.ag UI defaults to 300bps (3%).
 */
@Serializable
data class DynamicSlippage(
    val minBps: Int,
    val maxBps: Int
)
