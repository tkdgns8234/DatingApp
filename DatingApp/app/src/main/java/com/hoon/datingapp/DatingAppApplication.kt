package com.hoon.datingapp

import android.app.Application
import android.os.Build
import com.hoon.datingapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DatingAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR) // koin의 로그 레벨을 지정
            } else {
                androidLogger(Level.NONE)
            }
            androidContext(this@DatingAppApplication) // context 등록
            modules(appModule)
        }

    }
}