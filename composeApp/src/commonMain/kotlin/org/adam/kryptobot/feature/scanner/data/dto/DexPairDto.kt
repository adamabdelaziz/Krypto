package org.adam.kryptobot.feature.scanner.data.dto

import co.touchlab.kermit.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import java.math.BigDecimal
import java.text.DecimalFormat

@Serializable
data class DexPairDto(
    val schemaVersion: String? = null,
    val pairs: List<PairDto>? = null,
)

@Serializable
data class PairDto(
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
    @SerialName("txns") val transactions: Transactions? = null,
    val volume: Volume? = null,
    val priceChange: PriceChange? = null,
    val fdv: Double? = null,
    val marketCap: Double? = null,
    val pairCreatedAt: Double? = null,
    val info: Info? = null,
    val boosts: Boosts? = null
)

fun PairDto.toDexPair(oldList: List<DexPair>, initialList: List<DexPair>): DexPair {
    val oldOne = oldList.firstOrNull { it.pairAddress == this.pairAddress }
    val initialPair = initialList.firstOrNull { it.pairAddress == this.pairAddress }

    val initialPriceNative = initialPair?.priceNative?.toDoubleOrNull()
        ?: this.priceNative?.toDoubleOrNull()

    val recentPriceNative = oldOne?.priceNative?.toDoubleOrNull()
        ?: this.priceNative?.toDoubleOrNull()

    val priceChangeSinceScanned = if (initialPriceNative != null) {
        val newPriceNative = this.priceNative?.toDoubleOrNull()
        if (newPriceNative != null && initialPriceNative != 0.0) {
            ((newPriceNative - initialPriceNative) / initialPriceNative) * 100
        } else {
            0.0
        }
    } else {
        0.0
    }

    val recentPriceChangeSinceScanned = if (recentPriceNative != null) {
        val newPriceNative = this.priceNative?.toDoubleOrNull()
        if (newPriceNative != null && recentPriceNative != 0.0) {
            ((newPriceNative - recentPriceNative) / recentPriceNative) * 100
        } else {
            0.0
        }
    } else {
        0.0
    }

    //Base address used with RugChecker
    Logger.d("Base: ${this.baseToken?.address}")

    val ratio = this.liquidity?.usd?.let { usd->
        this.marketCap?.let { marketCap ->
            (usd.div(marketCap)).times(100)
        }
    } ?: 0.0

    return DexPair(
        chainId = chainId,
        dexId = dexId,
        url = url,
        pairAddress = pairAddress,
        labels = labels,
        baseToken = baseToken,
        quoteToken = quoteToken,
        priceNative = priceNative,
        priceUsd = priceUsd,
        liquidity = liquidity,
        transactions = transactions,
        volume = volume,
        priceChange = priceChange,
        fdv = fdv,
        marketCap = marketCap,
        pairCreatedAt = pairCreatedAt,
        info = info,
        boosts = boosts,
        priceChangeSinceScanned = priceChangeSinceScanned,
        recentPriceChangeSinceScanned = recentPriceChangeSinceScanned,
        beingTracked = oldOne?.beingTracked ?: false,
        liquidityMarketRatio = ratio,
    )
}


@Serializable
data class Transactions(
    val m5: TxCount? = null,
    val h1: TxCount? = null,
    val h6: TxCount? = null,
    val h24: TxCount? = null
)

@Serializable
data class TxCount(
    val buys: Int? = null,
    val sells: Int? = null
)

@Serializable
data class Volume(
    val h24: Double? = null,
    val h6: Double? = null,
    val h1: Double? = null,
    val m5: Double? = null
)

@Serializable
data class PriceChange(
    val m5: Double? = null,
    val h1: Double? = null,
    val h6: Double? = null,
    val h24: Double? = null
)

@Serializable
data class TokenDto(
    val address: String? = null,
    val name: String? = null,
    val symbol: String? = null
)

@Serializable
data class Liquidity(
    val usd: Double? = null,
    val base: Double? = null,
    val quote: Double? = null
)

@Serializable
data class Info(
    val imageUrl: String? = null,
    val websites: List<Website>? = null,
    val socials: List<Social>? = null
)

@Serializable
data class Website(
    val url: String? = null
)

@Serializable
data class Social(
    val type: String? = null,
    val url: String? = null
)

@Serializable
data class Boosts(
    val active: Int? = null
)
