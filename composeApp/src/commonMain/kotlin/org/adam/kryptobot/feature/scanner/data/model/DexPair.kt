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
    val recentPriceChangeSinceScanned: Double = 0.000000000000000,
    val liquidityMarketRatio: Double = 0.0,
)

fun calculateLiquidityScore(pair: DexPair): Double {
    val liquidityUsd = pair.liquidity?.usd ?: 0.0
    val liquidityBase = pair.liquidity?.base ?: 0.0
    val liquidityQuote = pair.liquidity?.quote ?: 0.0

    val volume = pair.volume?.h24 ?: 0.0

    val transactionCount = pair.transactions?.h24?.buys ?: (0 + (pair.transactions?.h24?.sells ?: 0))

    val liquidityWeight = 0.4
    val volumeWeight = 0.3
    val transactionCountWeight = 0.3

    val liquidityFactor = liquidityUsd / 1_000_000
    val volumeFactor = volume / 1_000_000
    val transactionCountFactor = transactionCount.toDouble() / 100

    val liquidityScore = (liquidityFactor * liquidityWeight) +
            (volumeFactor * volumeWeight) +
            (transactionCountFactor * transactionCountWeight)

    return liquidityScore
}
