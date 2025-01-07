package org.adam.kryptobot.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import org.adam.kryptobot.feature.swapper.ui.model.TransactionUiModel
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.BasicCard
import org.adam.kryptobot.ui.components.BasicText
import org.adam.kryptobot.ui.components.CenteredRow
import org.adam.kryptobot.ui.theme.CurrentColors
import org.adam.kryptobot.util.formatToDecimalString

@Composable
fun TransactionView(modifier: Modifier, transaction: TransactionUiModel, onClick: () -> Unit) {
    BasicCard(modifier) {
        CenteredRow(modifier = Modifier.fillMaxWidth().background(CurrentColors.surface)) {
            Column(modifier = Modifier.weight(3f)) {
                CenteredRow(modifier = Modifier.padding(4.dp)) {
                    BasicText(
                        text = transaction.inSymbol,
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
                        text = transaction.outSymbol,
                        color = CurrentColors.onSurface,
                    )
                    BasicText(
                        modifier = Modifier.padding(end = 4.dp),
                        text = transaction.transactionStep.name,
                        color = CurrentColors.onSurface,
                    )
                    BasicText(
                        modifier = Modifier.padding(end = 4.dp),
                        text = transaction.swapMode.name,
                        color = CurrentColors.onSurface,
                    )
                    BasicText(
                        modifier = Modifier.padding(end = 4.dp),
                        text = transaction.amount,
                        color = CurrentColors.onSurface,
                    )
                }
                CenteredRow(modifier = Modifier.padding(4.dp)) {
                    CenteredRow(modifier = Modifier.weight(1f)) {
                        BasicText(
                            modifier = Modifier.padding(end = 4.dp),
                            text = "Amounts:",
                            color = CurrentColors.onSurface,
                        )
                        BasicText(
                            text = transaction.inAmount,
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
                            text = transaction.outAmount,
                            color = CurrentColors.onSurface,
                        )
                        BasicText(
                            modifier = Modifier.padding(end = 4.dp),
                            text = "Fee: ${transaction.feesLamport}",
                            color = CurrentColors.onSurface,
                        )
                    }

                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                BasicButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    text = "Swap",
                    onClick = onClick
                )
            }
        }

    }
}