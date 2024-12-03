package org.adam.kryptobot.feature.scanner.data.model

import kotlinx.serialization.SerialName
import org.adam.kryptobot.feature.scanner.data.dto.Boosts
import org.adam.kryptobot.feature.scanner.data.dto.Info
import org.adam.kryptobot.feature.scanner.data.dto.Liquidity
import org.adam.kryptobot.feature.scanner.data.dto.PriceChange
import org.adam.kryptobot.feature.scanner.data.dto.TokenDto
import org.adam.kryptobot.feature.scanner.data.dto.Transactions
import org.adam.kryptobot.feature.scanner.data.dto.Volume

data class DexPair(
    val chainId: String? = null,
    val dexId: String? = null,
    val url: String? = null,
    val pairAddress: String? = null,
    val labels: List<String>? = null,
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
    val pairCreatedAt: Double? = null,
    val info: Info? = null,
    val boosts: Boosts? = null,
    val priceChangeSinceScanned: Double  = 0.000000000000000,
    val initialPriceNative: String? = null, //TODO Likely dont do this and just store initial of each unique pair
)
