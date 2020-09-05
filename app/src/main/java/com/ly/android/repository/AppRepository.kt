package com.ly.android.repository

/**
 * Created by Lan Yang on 2020/9/4
 *
 * 项目中所有的数据仓库都必须继承该类
 */
abstract class AppRepository {

    protected val tag by lazy {
        this.javaClass.simpleName
    }

    val sharedPref by lazy {
        SharedPreferencesManager
    }
}