package com.ly.android

import android.app.Application
import com.ly.module.basic.handler.CloseProcessHandler
import com.ly.module.log.isPrintLog

/**
 * Created by Lan Yang on 2020/9/4
 */
class AppApplication : Application() {

    companion object {
        lateinit var app: AppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        app = this

        isPrintLog = BuildConfig.DEBUG
        //解决应用崩溃后重启的问题
        CloseProcessHandler.init()
    }
}