package org.adam.kryptobot.ui.components.snackbar

import androidx.compose.material.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration

interface SnackbarManager {
    val messages: SharedFlow<SnackbarMessage>
    fun showMessage(
        message: String,
        actionLabel: String? = null,
        onActionPerformed: () -> Unit = {},
        onDismissed: () -> Unit = {},
    )
}

class SnackbarManagerImpl(private val snackScope: CoroutineScope) : SnackbarManager {
    private val _messages = MutableSharedFlow<SnackbarMessage>()
    override val messages: SharedFlow<SnackbarMessage> = _messages

    override fun showMessage(
        message: String,
        actionLabel: String?,
        onActionPerformed: () -> Unit,
        onDismissed: () -> Unit,
    ) {
        snackScope.launch {
            _messages.emit(SnackbarMessage(message, actionLabel, onActionPerformed, onDismissed))
        }
    }
}

data class SnackbarMessage(
    val message: String,
    val actionLabel: String?,
    val onActionPerformed: () -> Unit,
    val onDismissed: () -> Unit,
)