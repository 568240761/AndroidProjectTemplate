package com.ly.android.repository

import android.content.Context
import com.ly.android.AppApplication

/**
 * Created by Lan Yang on 2020/8/3
 * 管理应用中存储在xml文件中的数据
 */
object SharedPreferencesManager {

    private val sp = AppApplication.app.getSharedPreferences("app_data", Context.MODE_PRIVATE)

    fun getString(key: String, value: String = "") = sp.getString(key, value)!!

    fun setString(key: String, value: String) = sp.edit().putString(key, value).apply()
}