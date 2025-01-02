package org.adam.kryptobot.di.module

import org.adam.kryptobot.di.MAIN_SCOPE
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.ui.components.snackbar.SnackbarManagerImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<SnackbarManager> {
        SnackbarManagerImpl(
            snackScope = get(named(MAIN_SCOPE))
        )
    }
}