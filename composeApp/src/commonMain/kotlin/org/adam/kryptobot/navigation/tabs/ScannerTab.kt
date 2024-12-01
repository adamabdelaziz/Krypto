package org.adam.kryptobot.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.adam.kryptobot.feature.scanner.screens.ScannerScreen

object ScannerTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Scanner"

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = null
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = ScannerScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}