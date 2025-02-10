package org.adam.kryptobot.feature.telegram.ui.screens

sealed class TelegramTrackerScreenEvent {
    data class OnPhoneNumberEntered(val phoneNumber: String) : TelegramTrackerScreenEvent()
    data object SubmitPhoneNumber: TelegramTrackerScreenEvent()
    data class OnCodeEntered(val code: String) : TelegramTrackerScreenEvent()
    data object SubmitCode: TelegramTrackerScreenEvent()
    data class OnPasswordEntered(val password: String) : TelegramTrackerScreenEvent()
    data object SubmitPassword: TelegramTrackerScreenEvent()

}