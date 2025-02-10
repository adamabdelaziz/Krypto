package org.adam.kryptobot.feature.telegram.ui.screens

import co.touchlab.kermit.Logger
import org.drinkless.tdlib.TdApi

data class TelegramTrackerScreenUiState(
    val authStep: TelegramAuthStep? = null,
    val phoneNumber: String = "",
    val twoFactorCode: String = "",
    val password: String = "",
)

enum class TelegramAuthStep {
    NEEDS_PHONE_NUMBER,
    NEEDS_PASSWORD,
    NEEDS_CODE,
    LOGGED_IN,
}

fun TdApi.AuthorizationState?.toTelegramAuthStep(): TelegramAuthStep {
    when (this) {
        is TdApi.AuthorizationStateWaitPhoneNumber -> {
            return TelegramAuthStep.NEEDS_PHONE_NUMBER
        }

        is TdApi.AuthorizationStateWaitCode -> {
            return TelegramAuthStep.NEEDS_CODE
        }

        is TdApi.AuthorizationStateWaitPassword -> {
            return TelegramAuthStep.NEEDS_PASSWORD
        }

        is TdApi.AuthorizationStateReady -> {
            return TelegramAuthStep.LOGGED_IN
        }

        else -> {
            Logger.d("No Auth Mapper for ${this?.javaClass?.name} ")
            return TelegramAuthStep.LOGGED_IN
        }
    }
}

fun mapTelegramScreenUiState(
    authState: TdApi.AuthorizationState?,
    phoneNumber: String,
    twoFactorCode: String,
    password: String,
): TelegramTrackerScreenUiState {
    return TelegramTrackerScreenUiState(
        authStep = authState.toTelegramAuthStep(),
        phoneNumber = phoneNumber,
        twoFactorCode = twoFactorCode,
        password = password,
    )
}

