package com.ly.module.basic.manager

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat

/**
 * Created by Lan Yang on 2020/8/20
 * 管理动态权限
 */
object PermissionManager : ActivityCompat.OnRequestPermissionsResultCallback {

    private const val REQUEST_CODE_FOR_PERMISSION = 100

    /**需要申请的权限*/
    private var permissionList: List<Permission>? = null

    /**申请的权限全部同意之后的回调*/
    private var grantedCallback: (() -> Unit)? = null

    /**
     * 检查动态权限
     *
     * @param activity 该类的父类中必须包含[BasicActivity]
     * @param permissions 需要申请的权限
     * @param granted 申请的权限全部同意之后的回调
     */
    @Suppress("KDocUnresolvedReference")
    @MainThread
    fun checkPermission(
        activity: Activity,
        permissions: ArrayList<Permission>,
        granted: (() -> Unit)? = null
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//小于android 6.0
            granted?.invoke()
        } else {
            permissionList = permissions.filter {
                //判断是否已经拥有该权限
                ActivityCompat.checkSelfPermission(
                    activity,
                    it.permission
                ) == PackageManager.PERMISSION_DENIED
            }

            if (!permissionList.isNullOrEmpty()) {
                val array = permissionList!!.map { it.permission }
                grantedCallback = granted
                activity.requestPermissions(array.toTypedArray(), REQUEST_CODE_FOR_PERMISSION)
            } else {//权限都已经允许
                granted?.invoke()

                permissionList = null
                grantedCallback = null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_FOR_PERMISSION) {
            var pass = true
            for ((index, result) in grantResults.withIndex()) {
                if (result == PackageManager.PERMISSION_DENIED) {//被拒绝
                    if (pass) pass = false
                    permissionList?.get(index)?.denied?.invoke()
                } else {
                    permissionList?.get(index)?.granted?.invoke()
                }
            }

            if (pass)
                grantedCallback?.invoke()

            permissionList = null
            grantedCallback = null
        }
    }

    data class Permission(
        /**权限名称*/
        val permission: String,
        /**拒绝该权限的回调*/
        val denied: (() -> Unit)? = null,
        /**同意该权限的回调*/
        val granted: (() -> Unit)? = null
    )
}