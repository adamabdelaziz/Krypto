package org.adam.kryptobot.feature.wallet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.adam.kryptobot.feature.wallet.ui.screens.WalletScreenModel
import org.adam.kryptobot.ui.components.BasicButton
import org.adam.kryptobot.ui.components.InputTextField
import org.adam.kryptobot.ui.components.PasswordTextField
import org.adam.kryptobot.ui.theme.LocalAppColors
import org.adam.kryptobot.ui.theme.LocalAppTypography

class WalletScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<WalletScreenModel>()
        val state: WalletScreenUiState by screenModel.uiState.collectAsState(WalletScreenUiState())

        WalletScreenContent(state = state, onEvent = screenModel::onEvent)
    }

    @Composable
    fun WalletScreenContent(
        state: WalletScreenUiState, onEvent: (WalletScreenEvent) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(LocalAppColors.current.background)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            InputTextField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                text = state.wallet?.publicKey ?: "",
                onTextChanged = { onEvent(WalletScreenEvent.OnUpdatePublicKeyClicked(it)) },
                label = "Public Key"
            )

            PasswordTextField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                text = state.wallet?.privateAddress ?: "",
                onTextChanged = { onEvent(WalletScreenEvent.OnUpdatePrivateKeyClicked(it)) },
                isTextVisible = state.isPrivateKeyVisible,
                onVisibilityChanged = { onEvent(WalletScreenEvent.OnToggleVisibilityClicked) },
                label = "Private Key"
            )

            BasicButton(
                modifier = Modifier.padding(16.dp),
                onClick = { onEvent(WalletScreenEvent.OnRefreshBalanceClicked) },
                text = "Refresh Balance"
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                state.wallet?.tokenBalance?.let {
                    items(it) { (mint, balance) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Address :$mint",
                                style = LocalAppTypography.current.body1,
                                color = LocalAppColors.current.onBackground
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "Balance : $balance",
                                style = LocalAppTypography.current.body1,
                                color = LocalAppColors.current.onBackground
                            )
                        }
                    }
                }

            }
        }
    }
}