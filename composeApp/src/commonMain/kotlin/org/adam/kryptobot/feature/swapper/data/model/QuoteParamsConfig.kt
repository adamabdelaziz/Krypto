package org.adam.kryptobot.feature.swapper.data.model

import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.swapper.enum.SwapMode
import java.math.BigDecimal

data class QuoteParamsConfig(
//    val inputAddress: String,
//    val outputAddress: String,
    val safeMode: Boolean = true, //Extra button to confirm quote or not.
    val amount: BigDecimal  = BigDecimal(0.00001), // Double
    val slippageBps: Int = 100, //Int 1% slippage (100 basis points)
    val swapMode: SwapMode = SwapMode.ExactIn,
    val dexes: Set<Dex> = setOf(),
    val excludeDexes: Set<Dex> = setOf(Dex.Orca, Dex.Uniswap, Dex.Meteora),
    val restrictIntermediateTokens: Boolean = false,
    val onlyDirectRoutes: Boolean = false,
    val asLegacyTransaction: Boolean = false,
    val platformFeeBps: Int? = null, // Int
    val maxAccounts: Int? = null, // Int
    val autoSlippage: Boolean = false,
    val maxAutoSlippageBps: Int? = null, // Int
    val autoSlippageCollisionUsdValue: Int? = null, // Int
)
