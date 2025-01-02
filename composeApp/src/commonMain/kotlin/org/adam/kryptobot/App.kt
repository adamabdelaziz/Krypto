package org.adam.kryptobot

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.adam.kryptobot.navigation.tabs.ScannerTab
import org.adam.kryptobot.ui.AppScaffold
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.ui.components.snackbar.SnackbarManagerImpl
import org.adam.kryptobot.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.java.KoinJavaComponent.get

@Composable
@Preview
fun App() {
    AppTheme {
        KoinContext {
            val snackbarManager: SnackbarManager = get(SnackbarManager::class.java)
            TabNavigator(
                tab = ScannerTab
            ) {
                AppScaffold(
                    snackbarManager = snackbarManager
                )
            }
        }
    }
}