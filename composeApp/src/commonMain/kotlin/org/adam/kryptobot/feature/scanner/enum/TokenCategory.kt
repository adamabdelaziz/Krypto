package org.adam.kryptobot.feature.scanner.enum

enum class TokenCategory(private val displayName: String) {
    LatestBoosted("Latest Boosted Tokens"),
    MostActiveBoosted("Most Active Boosted Tokens"),
    Latest("Latest Tokens");

    override fun toString(): String {
        return displayName
    }
}