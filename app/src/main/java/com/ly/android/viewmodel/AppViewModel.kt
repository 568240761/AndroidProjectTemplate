package com.ly.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.module.util.log.logError
import com.ly.android.viewmodel.subviewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * Created by Lan Yang on 2020/9/4
 *
 * 项目中所有的[ViewModel]必须继承该类；
 *
 * 如果需要保存界面的状态，必须存在一个参数类型为 SavedStateHandle 的构造函数，如[MainViewModel]类
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