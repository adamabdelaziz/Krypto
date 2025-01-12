package org.adam.kryptobot.feature.swapper.data.model

import java.math.BigDecimal
import java.util.Objects

data class SwapStrategy(
    val profitTargetPct: BigDecimal = BigDecimal("20"),
    val stopLossPct: BigDecimal? = BigDecimal("20"),
    val trailingStopPct: BigDecimal? = BigDecimal("10"),
    val exitPct: BigDecimal? = null,
    val exitStrategy: ExitStrategy = ExitStrategy.SWAP_ALL,
    val key: String //Base Token Address
) {

    enum class ExitStrategy {
        SWAP_ALL,
        SWAP_PARTIAL,
        BREAK_EVEN
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SwapStrategy) return false

        return profitTargetPct == other.profitTargetPct &&
                stopLossPct == other.stopLossPct &&
                trailingStopPct == other.trailingStopPct &&
                exitPct == other.exitPct &&
                exitStrategy == other.exitStrategy &&
                key == other.key
    }

    override fun hashCode(): Int {
        return Objects.hash(profitTargetPct, stopLossPct, trailingStopPct, exitPct, exitStrategy, key)
    }
}
