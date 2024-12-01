package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DexPairDto(
    val schemaVersion: String,
    val pairs: List<Pair>? = null,
)

@Serializable
data class Pair(
    val chainId: String,
    val dexId: String,
    val url: String,
    val pairAddress: String,
    val labels: List<String>,
    val baseToken: Token,
    val quoteToken: Token,
    val priceNative: String,
    val priceUsd: String,
    val liquidity: Liquidity,
    val fdv: Long,
    val marketCap: Long,
    val pairCreatedAt: Long,
    val info: Info,
    val boosts: Boosts
)

@Serializable
data class Token(
    val address: String,
    val name: String,
    val symbol: String
)

@Serializable
data class Liquidity(
    val usd: Long,
    val base: Long,
    val quote: Long
)

@Serializable
data class Info(
    val imageUrl: String,
    val websites: List<Website>,
    val socials: List<Social>
)

@Serializable
data class Website(
    val url: String
)

@Serializable
data class Social(
    val platform: String,
    val handle: String
)

@Serializable
data class Boosts(
    val active: Int
)