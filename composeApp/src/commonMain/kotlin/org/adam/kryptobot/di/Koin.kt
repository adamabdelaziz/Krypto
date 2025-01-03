package org.adam.kryptobot.di

import org.adam.kryptobot.di.module.appModule
import org.adam.kryptobot.di.module.coroutineModule
import org.adam.kryptobot.di.module.dataModule
import org.adam.kryptobot.di.module.networkModule
import org.adam.kryptobot.di.module.repositoryModule
import org.adam.kryptobot.di.module.screenModelsModule
import org.adam.kryptobot.di.module.useCaseModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        //printLogger(Level.DEBUG)
        config?.invoke(this)
        modules(
            coroutineModule,
            appModule,
            networkModule,
            dataModule,
            repositoryModule,
            useCaseModule,
            screenModelsModule,
        )
    }
}

//Koin "named" properties go here
const val MAIN_SCOPE = "MainScope"
const val IO_SCOPE = "IoScope"
const val DEFAULT_SCOPE = "DefaultScope"