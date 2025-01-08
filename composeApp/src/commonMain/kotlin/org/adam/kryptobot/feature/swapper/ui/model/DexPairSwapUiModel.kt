package org.adam.kryptobot.feature.swapper.ui.model

import org.adam.kryptobot.feature.scanner.data.dto.Info
import org.adam.kryptobot.feature.scanner.data.dto.Liquidity
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto

/*
    TODO map all API objects to values that are actually being used
 */
data class DexPairSwapUiModel(
    val key: String, //TODO remove this but it was the Pair Address
    val dexId: String? = null,
    val baseToken: TokenDto? = null,
    val quoteToken: TokenDto? = null,
    val priceSol: String? = null,
    val priceUsd: String? = null,
    val liquidity: Liquidity? = null,
    val marketCap: Double? = null,
    val info: Info? = null,
    val priceChangeSinceScanned: Double  = 0.000000000000000,
    val recentPriceChangeSinceScanned: Double = 0.000000000000000,
    val liquidityMarketRatio: Double = 0.0,
)