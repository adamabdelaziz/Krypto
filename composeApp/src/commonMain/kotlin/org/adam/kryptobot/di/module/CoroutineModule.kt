package org.adam.kryptobot.di.module

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.adam.kryptobot.di.DEFAULT_SCOPE
import org.adam.kryptobot.di.IO_SCOPE
import org.adam.kryptobot.di.MAIN_SCOPE
import org.adam.kryptobot.ui.components.snackbar.SnackbarManager
import org.adam.kryptobot.ui.components.snackbar.SnackbarManagerImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
    single(named(MAIN_SCOPE)) { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single(named(IO_SCOPE)) { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single(named(DEFAULT_SCOPE)) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}