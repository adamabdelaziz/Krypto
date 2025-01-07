package org.adam.kryptobot.util

import org.adam.kryptobot.feature.swapper.enum.SwapMode
import java.math.BigDecimal

fun solToLamports(sol: BigDecimal): Long {
    return sol.multiply(BigDecimal(1_000_000_000)).toLong()
}

fun solToLamports(sol: String): Long {
    if (sol.isEmpty()) return 0L
    return BigDecimal(sol).multiply(BigDecimal(1_000_000_000)).toLong()
}

fun lamportsToSol(lamports: Long): BigDecimal {
    return BigDecimal(lamports).divide(BigDecimal(1_000_000_000))
}

fun lamportsToSol(lamports: String): BigDecimal {
    if (lamports.isEmpty()) return BigDecimal.ZERO
    return BigDecimal(lamports).divide(BigDecimal(1_000_000_000))
}

fun getSwapTokenAddresses(
    swapMode: SwapMode,
    baseTokenAddress: String,
    quoteTokenAddress: String
): Pair<String, String> {
    return when (swapMode) {
        SwapMode.ExactIn -> quoteTokenAddress to baseTokenAddress
        SwapMode.ExactOut -> baseTokenAddress to quoteTokenAddress
    }
}