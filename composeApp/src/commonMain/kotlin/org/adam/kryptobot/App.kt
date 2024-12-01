package org.adam.kryptobot

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.adam.kryptobot.navigation.tabs.ScannerTab
import org.adam.kryptobot.ui.components.BottomNavScaffold
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            TabNavigator(
                tab = ScannerTab
            ) {
                BottomNavScaffold()
            }
        }
    }
}