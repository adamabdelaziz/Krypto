package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.enum.SwapMode

data class QuoteParamsConfig(
//    val inputAddress: String,
//    val outputAddress: String,
    val amount: Double = 0.0,
    val slippageBps: Int = 10000,
    val swapMode: SwapMode = SwapMode.ExactOut,
    val dexes: Set<Dex> = setOf(), //TODO likely change these two to Dex enum
    val excludeDexes: Set<Dex> = setOf(),
    val restrictIntermediateTokens: Boolean = false,
    val onlyDirectRoutes: Boolean = false,
    val asLegacyTransaction: Boolean = false,
    val platformFeeBps: Int? = null,
    val maxAccounts: Int? = null,
    val autoSlippage: Boolean = false,
    val maxAutoSlippageBps: Int? = null,
    val autoSlippageCollisionUsdValue: Int? = null,
)
