package com.ly.android.ui.subui

import android.os.Bundle
import androidx.activity.viewModels
import com.ly.android.R
import com.ly.android.ui.AppActivity
import com.ly.android.viewmodel.subviewmodel.MainViewModel

class MainActivity : AppActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}