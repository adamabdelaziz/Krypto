package org.adam.kryptobot.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
    single(named("MainScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single(named("IoScope")) { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single(named("DefaultScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}