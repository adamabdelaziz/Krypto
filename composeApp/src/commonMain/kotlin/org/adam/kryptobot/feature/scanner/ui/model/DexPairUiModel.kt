package org.adam.kryptobot.feature.scanner.ui.model

import org.adam.kryptobot.feature.scanner.data.dto.Info
import org.adam.kryptobot.feature.scanner.data.dto.Liquidity
import org.adam.kryptobot.feature.scanner.data.dto.PriceChange
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto
import org.adam.kryptobot.feature.scanner.data.dto.Transactions
import org.adam.kryptobot.feature.scanner.data.dto.Volume

data class DexPairUiModel(
    val dexId: String? = null,
    val baseToken: TokenDto? = null,
    val quoteToken: TokenDto? = null,
    val priceNative: String? = null,
    val priceUsd: String? = null,
    val liquidity: Liquidity? = null,
    val transactions: Transactions? = null,
    val volume: Volume? = null,
    val priceChange: PriceChange? = null,
    val fdv: Double? = null,
    val marketCap: Double? = null,
    val info: Info? = null,
    val priceChangeSinceScanned: Double  = 0.000000000000000,
    val recentPriceChangeSinceScanned: Double = 0.000000000000000,
    val beingTracked: Boolean = false,
    val liquidityMarketRatio: Double = 0.0,
)