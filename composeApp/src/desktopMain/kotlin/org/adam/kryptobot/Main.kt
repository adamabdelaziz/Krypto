package org.adam.kryptobot

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import co.touchlab.kermit.Logger
import org.adam.kryptobot.di.initKoin

fun main() {
    initKoin()
    Logger.d("Monkas")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KryptoBotzzz",
        ) {
            App()
        }
    }
}