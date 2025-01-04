package org.adam.kryptobot.feature.swapper.data.model

data class QuoteParamsConfig(
//    val inputAddress: String,
//    val outputAddress: String,
//    val amount: Double,
    val slippageBps: Int = 10000,
    val swapMode: String = "ExactOut",
    val dexes: List<String>? = null, //TODO likely change these two to Dex enum
    val excludeDexes: List<String>? = null,
    val restrictIntermediateTokens: Boolean = false,
    val onlyDirectRoutes: Boolean = false,
    val asLegacyTransaction: Boolean = false,
    val platformFeeBps: Int? = null,
    val maxAccounts: Int? = null,
    val autoSlippage: Boolean = false,
    val maxAutoSlippageBps: Int? = null,
    val autoSlippageCollisionUsdValue: Int? = null,
)
