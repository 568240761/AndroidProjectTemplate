package com.ly.android.ui.subui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ly.android.AppApplication
import com.ly.android.R
import com.ly.android.ui.AppActivity
import com.ly.android.viewmodel.subviewmodel.MainViewModel
import com.ly.module.storage.StorageUtil

class MainActivity : AppActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launchWhenCreated {
            val list = StorageUtil.queryImages(AppApplication.app)
            list.forEach { println(it) }

            val videos = StorageUtil.queryVideos(AppApplication.app)
            videos.forEach { println(it) }

            val audios = StorageUtil.queryAudios(AppApplication.app)
            audios.forEach { println(it) }
        }
    }
}