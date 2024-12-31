package org.adam.kryptobot.ui.components

import WalletTab
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.adam.kryptobot.navigation.tabs.ScannerTab
import org.adam.kryptobot.navigation.tabs.SwapperTab
import org.adam.kryptobot.ui.components.snackbar.AppSnackbar
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager

@Composable
fun BottomNavScaffold(
    snackbarManager: SnackbarManager = SnackbarManager
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(snackbarManager.messages) {
        snackbarManager.messages.collect { snackbarMessage ->
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = snackbarMessage.message,
                actionLabel = snackbarMessage.actionLabel
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    snackbarMessage.onActionPerformed
                }

                SnackbarResult.Dismissed -> {
                    snackbarMessage.onDismissed
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(
                hostState = scaffoldState.snackbarHostState,
                snackbar = { data ->
                    AppSnackbar(snackbarData = data)
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            /*
                TODO: Replace with my own
             */
            BottomNavigation(backgroundColor = Color.Black, elevation = 32.dp) {
                TabNavigationItem(ScannerTab)
                TabNavigationItem(SwapperTab)
                TabNavigationItem(WalletTab)
            }
        },
        content = { CurrentTab() },
    )
}


@Composable
fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp).padding(vertical = 5.dp),
                    painter = icon,
                    contentDescription = tab.options.title
                )
            }
        },
        label = {
            Text(text = tab.options.title, color = Color.White)
        }
    )
}