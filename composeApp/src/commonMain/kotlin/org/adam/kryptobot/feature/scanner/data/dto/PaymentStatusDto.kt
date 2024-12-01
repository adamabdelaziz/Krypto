package org.adam.kryptobot.feature.scanner.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusDto(
    val type: String,
    val status: String,
    val paymentTimestamp: Long
)