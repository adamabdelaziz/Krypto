package org.adam.kryptobot.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.ui.components.BasicCard
import org.adam.kryptobot.ui.components.BasicText
import org.adam.kryptobot.ui.components.CenteredRow
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.util.formatToDecimalString

@Composable
fun TransactionView(modifier: Modifier, transaction: TransactionUiModel, onClick: () -> Unit) {
    BasicCard(modifier) {
        Column(modifier = Modifier.fillMaxWidth().background(CurrentColors.surface)) {
            CenteredRow(modifier = Modifier.padding(4.dp)) {
                BasicText(
                    text = transaction.inputSymbol,
                    color = CurrentColors.onSurface,
                )
                Icon(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    imageVector = FeatherIcons.ArrowRight,
                    contentDescription = null,
                    tint = CurrentColors.secondary
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.outputSymbol,
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.amount,
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "Lamports:",
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    text = transaction.inputAmount,
                    color = CurrentColors.onSurface,
                )
                Icon(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    imageVector = FeatherIcons.ArrowRight,
                    contentDescription = null,
                    tint = CurrentColors.secondary
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.outputAmount,
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.transactionStep.name,
                    color = CurrentColors.onSurface,
                )
            }
            CenteredRow(modifier = Modifier.padding(4.dp)) {
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.platformFeeAmount,
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.platformFeeBps.formatToDecimalString(),
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.slippageBps.toString(),
                    color = CurrentColors.onSurface,
                )
                BasicText(
                    modifier = Modifier.padding(end = 4.dp),
                    text = transaction.swapMode.name,
                    color = CurrentColors.onSurface,
                )
            }
        }
    }
}