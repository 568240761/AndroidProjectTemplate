package com.ly.module.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ly.module.basic.manager.ActivityManager
import com.ly.module.basic.manager.ObservableManager
import com.ly.module.basic.manager.PermissionManager

/**
 * Created by Lan Yang on 2020/8/20
 * 项目里所有[Activity]的父类
 */
@Suppress("KDocUnresolvedReference")
class BasicActivity : AppCompatActivity(), ObservableManager.Observer {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.addActivity(this)
        ObservableManager.registerObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.removeActivity(this)
        ObservableManager.unregisterObserver(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onChange(type: Int, data: Any?) {}
}