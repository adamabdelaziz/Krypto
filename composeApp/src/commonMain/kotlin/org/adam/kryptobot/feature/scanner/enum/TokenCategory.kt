package org.adam.kryptobot.feature.scanner.enum

enum class TokenCategory(private val displayName: String) {
    LATEST_BOOSTED("Latest Boosted Tokens"),
    MOST_ACTIVE_BOOSTED("Most Active Boosted Tokens"),
    LATEST("Latest Tokens");

    override fun toString(): String {
        return displayName
    }
}