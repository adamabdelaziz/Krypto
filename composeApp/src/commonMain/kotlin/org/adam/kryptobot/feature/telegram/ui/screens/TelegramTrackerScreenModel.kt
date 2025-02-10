package org.adam.kryptobot.feature.telegram.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zhuinden.flowcombinetuplekt.combineStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.adam.kryptobot.feature.telegram.data.TelegramApi

class TelegramTrackerScreenModel(
    private val telegramApi: TelegramApi
) : ScreenModel {

    private val _phoneNumber = MutableStateFlow("")
    private val _twoFactorCode = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val uiState: StateFlow<TelegramTrackerScreenUiState> = combineStates(
        screenModelScope,
        SharingStarted.WhileSubscribed(),
        telegramApi.authState,
        _phoneNumber,
        _twoFactorCode,
        _password,
        ::mapTelegramScreenUiState,
    )

    fun onEvent(event: TelegramTrackerScreenEvent) {
        when (event) {
            is TelegramTrackerScreenEvent.OnCodeEntered -> {
                _twoFactorCode.value = event.code
            }

            is TelegramTrackerScreenEvent.OnPasswordEntered -> {
                _password.value = event.password

            }

            is TelegramTrackerScreenEvent.OnPhoneNumberEntered -> {
                _phoneNumber.value = event.phoneNumber
            }

            TelegramTrackerScreenEvent.SubmitCode -> {
                screenModelScope.launch {
                    telegramApi.authenticateWithCode(_twoFactorCode.value)
                }
            }

            TelegramTrackerScreenEvent.SubmitPassword -> {
                screenModelScope.launch {
                    telegramApi.authenticateWithPassword(_password.value)
                }
            }

            TelegramTrackerScreenEvent.SubmitPhoneNumber -> {
                screenModelScope.launch {
                    telegramApi.authenticateClient(_phoneNumber.value)
                }
            }
        }
    }
}