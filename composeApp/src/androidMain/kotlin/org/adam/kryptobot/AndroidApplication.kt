package org.adam.kryptobot

import android.app.Application
import org.adam.kryptobot.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@AndroidApplication)
        }
    }
}
