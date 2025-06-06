package org.adam.kryptobot.feature.scanner.data.mappers

import org.adam.kryptobot.feature.scanner.data.dto.PairDto
import org.adam.kryptobot.feature.scanner.data.model.DexPair
import org.adam.kryptobot.feature.scanner.data.model.calculateLiquidityScore
import org.adam.kryptobot.feature.scanner.ui.model.DexPairScanUiModel
import org.adam.kryptobot.feature.swapper.ui.model.DexPairSwapUiModel

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

fun DexPair.toDexPairScanUiModel(isBeingTracked: Boolean): DexPairScanUiModel {
    return DexPairScanUiModel(
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

/*
    Similar param above for if its being actively swapped
 */
fun DexPair.toDexPairSwapUiModel(): DexPairSwapUiModel {
    return DexPairSwapUiModel(
        key = this.pairAddress ?: "NULL",
        dexId = this.dexId,
        baseToken = this.baseToken,
        quoteToken = this.quoteToken,
        priceSol = this.priceNative,
        priceUsd = this.priceUsd,
        liquidity = this.liquidity,
        marketCap = this.marketCap,
        info = this.info,
        priceChangeSinceScanned = this.priceChangeSinceScanned,
        recentPriceChangeSinceScanned = this.recentPriceChangeSinceScanned,
        liquidityMarketRatio = this.liquidityMarketRatio,
        liquidityScore = calculateLiquidityScore(this)
    )
}