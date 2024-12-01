package org.adam.kryptobot.di

import org.adam.kryptobot.feature.scanner.screens.ScannerScreenModel
import org.koin.dsl.module

val screenModelsModule = module {
    factory {
        ScannerScreenModel(get())
    }
}