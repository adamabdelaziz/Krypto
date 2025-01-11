package org.adam.kryptobot.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun <T : Screen> HandleScreenEnter(navigator: Navigator, screen: T, onEnter: () -> Unit) {
    LaunchedEffect(navigator) {
        snapshotFlow { navigator.lastItem }
            .collect { lastScreen ->
                if (lastScreen == screen) {
                    onEnter()
                }
            }
    }
}