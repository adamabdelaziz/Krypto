package org.adam.kryptobot.feature.scanner.data.mappers

import org.adam.kryptobot.feature.scanner.data.dto.Boosts
import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.enum.Dex
import org.adam.kryptobot.feature.scanner.ui.model.DexPairUiModel

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
    //Logger.d("Base: ${this.baseToken?.address}")

    val ratio = this.liquidity?.usd?.let { usd ->
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
        liquidityMarketRatio = ratio,
    )
}

fun DexPair.toDexPairUiModel(isBeingTracked: Boolean): DexPairUiModel {
    return DexPairUiModel(
        dexId = this.dexId,
        baseToken = this.baseToken,
        quoteToken = this.quoteToken,
        priceNative = this.priceNative,
        priceUsd = this.priceUsd,
        liquidity = this.liquidity,
        transactions = this.transactions,
        volume = this.volume,
        priceChange = this.priceChange,
        fdv = this.fdv,
        marketCap = this.marketCap,
        info = this.info,
        priceChangeSinceScanned = this.priceChangeSinceScanned,
        recentPriceChangeSinceScanned = this.recentPriceChangeSinceScanned,
        beingTracked = isBeingTracked,
        liquidityMarketRatio = this.liquidityMarketRatio,
    )
}
