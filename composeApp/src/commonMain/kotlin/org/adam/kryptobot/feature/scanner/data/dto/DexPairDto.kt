package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DexPairDto(
    val schemaVersion: String? = null,
    val pairs: List<Pair>? = null,
)

@Serializable
data class Pair(
    val chainId: String? = null,
    val dexId: String? = null,
    val url: String? = null,
    val pairAddress: String? = null,
    val labels: List<String>? = null,
    val baseToken: Token? = null,
    val quoteToken: Token? = null,
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
data class Token(
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
