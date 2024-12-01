package org.adam.kryptobot

import androidx.compose.ui.window.ComposeUIViewController
import org.adam.kryptobot.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}