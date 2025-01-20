package org.adam.kryptobot.feature.swapper.data.model

import co.touchlab.kermit.Logger
import java.math.BigDecimal

data class TrackedTransaction(
    val transaction: Transaction,
    val highestObservedPriceSol: BigDecimal,
    val isCompleted: Boolean = false,
) {
    fun updatePrice(newPrice: BigDecimal): TrackedTransaction {
        return if (newPrice > highestObservedPriceSol) {
            copy(highestObservedPriceSol = newPrice)
        } else {
            this
        }
    }

    fun shouldExit(currentPrice: BigDecimal, strategy: SwapStrategy): Boolean {
        //if (isCompleted) return false

        val profitTargetPrice = transaction.initialDexPriceSol * (BigDecimal.ONE + (strategy.profitTargetPct / BigDecimal(100)))
        val stopLossPrice = transaction.initialDexPriceSol * (BigDecimal.ONE - (strategy.stopLossPct ?: BigDecimal.ZERO) / BigDecimal(100))
        val trailingStopPrice = highestObservedPriceSol * (BigDecimal.ONE - (strategy.trailingStopPct ?: BigDecimal.ZERO) / BigDecimal(100))
        Logger.d("Initial price is ${transaction.initialDexPriceSol} current is $currentPrice profit target is $profitTargetPrice stop loss is $stopLossPrice trailing stop is $trailingStopPrice")
        return when {
            currentPrice >= profitTargetPrice -> true
            currentPrice <= stopLossPrice -> true
            strategy.trailingStopPct != null && currentPrice <= trailingStopPrice -> true
            else -> false
        }
    }

    fun markAsCompleted(): TrackedTransaction = copy(isCompleted = true)
}