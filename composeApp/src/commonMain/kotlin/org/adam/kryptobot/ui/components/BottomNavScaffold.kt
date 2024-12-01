package org.adam.kryptobot.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.adam.kryptobot.navigation.tabs.ScannerTab

@Composable
fun BottomNavScaffold() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            /*
                TODO: Replace with my own
             */
            BottomNavigation(backgroundColor = Color.Black, elevation = 32.dp) {
                TabNavigationItem(ScannerTab)
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