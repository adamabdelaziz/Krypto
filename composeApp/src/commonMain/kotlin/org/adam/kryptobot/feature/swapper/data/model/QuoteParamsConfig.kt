package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.enum.SwapMode

data class QuoteParamsConfig(
//    val inputAddress: String,
//    val outputAddress: String,
    val amount: Double = 0.0, // Double
    val slippageBps: Int = 1000, // Int
    val swapMode: SwapMode = SwapMode.ExactOut,
    val dexes: Set<Dex> = setOf(),
    val excludeDexes: Set<Dex> = setOf(),
    val restrictIntermediateTokens: Boolean = false,
    val onlyDirectRoutes: Boolean = false,
    val asLegacyTransaction: Boolean = false,
    val platformFeeBps: Int? = null, // Int
    val maxAccounts: Int? = null, // Int
    val autoSlippage: Boolean = false,
    val maxAutoSlippageBps: Int? = null, // Int
    val autoSlippageCollisionUsdValue: Int? = null, // Int
)
