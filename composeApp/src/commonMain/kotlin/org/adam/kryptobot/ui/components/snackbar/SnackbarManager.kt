package org.adam.kryptobot.ui.components.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object SnackbarManager {
    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages: SharedFlow<SnackbarMessage> = _messages

    suspend fun showMessage(
        message: String,
        actionLabel: String? = null,
        onActionPerformed: () -> Unit = {},
        onDismissed: () -> Unit = {}
    ) {
        _messages.emit(SnackbarMessage(message, actionLabel, onActionPerformed, onDismissed))
    }
}

data class SnackbarMessage(
    val message: String,
    val actionLabel: String?,
    val onActionPerformed: () -> Unit,
    val onDismissed: () -> Unit,
)