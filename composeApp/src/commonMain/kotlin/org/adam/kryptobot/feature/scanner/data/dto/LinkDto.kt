package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LinkDto(
    val type: String? = null,
    val label: String? = null,
    val url: String? = null,
)