package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JupiterSwapParametersDto(
    val wrapAndUnwrapSol: Boolean = true,
    val useSharedAccounts: Boolean,
    val feeAccount: String, //Fee token account, it can be either the input mint or the output mint for ExactIn and only the input mint for ExactOut
    val trackingAccount: String?,
    val computeUnitPriceMicroLamports: Long?,
    val prioritizationFeeLamports: Long,
    val asLegacyTransaction: Boolean = false,
    val useTokenLedger: Boolean = false,
    val destinationTokenAccount: String,
    val dynamicComputeUnitLimit: Boolean = false,
    val skipUserAccountsRpcCalls: Boolean = false,
    val dynamicSlippage: DynamicSlippage = DynamicSlippage(
        minBps = 100,
        maxBps = 300,
    )
)

/*
    The user max slippage, note that jup.ag UI defaults to 300bps (3%).
 */
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
    val swapParameters: JupiterSwapParametersDto,
    val quoteResponse: JupiterQuoteDto,
)