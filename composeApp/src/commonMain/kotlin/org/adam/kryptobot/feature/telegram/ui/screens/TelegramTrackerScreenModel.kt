package org.adam.kryptobot.feature.telegram.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.StateFlow

class TelegramTrackerScreenModel(

) : ScreenModel {

    val uiState: StateFlow<TelegramTrackerScreenUiState> = .stateIn(
    scope = screenModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = TelegramTrackerScreenUiState()
    )

    fun onEvent(event: TelegramTrackerScreenEvent) {
        when (event) {

        }
    }
}