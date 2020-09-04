package com.ly.android.ui.subclass

import android.os.Bundle
import com.ly.android.R
import com.ly.android.ui.AppActivity

class MainActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}