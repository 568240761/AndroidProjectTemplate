package com.ly.android.viewmodel.subviewmodel

import androidx.lifecycle.SavedStateHandle
import com.ly.android.AppApplication
import com.ly.android.viewmodel.AppViewModel
import com.ly.module.network.RequestBuilder
import com.ly.module.util.log.logDebug

/**
 * Created by Lan Yang on 2020/9/5
 */
class MainViewModel(private val savedStateHandle: SavedStateHandle) : AppViewModel() {}