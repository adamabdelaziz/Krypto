package org.adam.kryptobot.feature.swapper.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SwapMode {
    @SerialName("ExactIn")
    ExactIn,

    @SerialName("ExactOut")
    ExactOut
}