package org.adam.kryptobot.ui.components.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarData
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.theme.LocalAppColors
import org.adam.kryptobot.ui.theme.LocalAppShapes
import org.adam.kryptobot.ui.theme.LocalAppTypography

@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalAppColors.current.primary,
    contentColor: Color = LocalAppColors.current.onPrimary,
    actionColor: Color = LocalAppColors.current.secondary
) {
    Surface(
        modifier = modifier.padding(16.dp),
        shape = LocalAppShapes.current.medium,
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = snackbarData.message,
                color = contentColor,
                style = LocalAppTypography.current.body2,
                modifier = Modifier.weight(1f)
            )
            snackbarData.actionLabel?.let { actionLabel ->
                BasicButton(
                    onClick = { snackbarData.performAction() },
                    text = actionLabel,
                    textColor = actionColor
                )
            }
        }
    }
}