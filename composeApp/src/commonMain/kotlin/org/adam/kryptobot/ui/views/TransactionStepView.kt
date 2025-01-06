package org.adam.kryptobot.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.adam.kryptobot.feature.swapper.data.model.Transaction
import org.adam.kryptobot.ui.components.BasicCard
import org.adam.kryptobot.ui.components.CenteredRow

@Composable
fun TransactionStepView(modifier: Modifier, transactionStep: Transaction, onClick: () -> Unit) {
    BasicCard(modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CenteredRow {

            }
        }
    }
}