package com.ly.android

import android.app.Application

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
    }
}