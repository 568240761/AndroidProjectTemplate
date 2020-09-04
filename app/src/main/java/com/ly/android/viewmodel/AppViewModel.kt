package com.ly.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.module.util.log.logError
import kotlinx.coroutines.launch

/**
 * Created by Lan Yang on 2020/9/4
 *
 * 项目中所有的[ViewModel]必须继承该类
 */
abstract class AppViewModel : ViewModel() {

    protected val tag by lazy {
        this.javaClass.simpleName
    }

    /**
     * 该方法可用于执行耗时的任务，比如网络请求
     */
    fun launch(
        block: suspend () -> Unit,
        error: suspend () -> Unit = {}
    ) = viewModelScope.launch {
        try {
            block()
        } catch (e: Exception) {
            logError(tag, "", e)
            error()
        }
    }
}