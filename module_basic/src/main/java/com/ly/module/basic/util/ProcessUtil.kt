package com.ly.module.basic.util

import android.app.ActivityManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.ly.module.log.logDebug
import com.ly.module.log.logError

/**
 * Created by Lan Yang on 2020/8/20
 * 进程方面的工具类
 */
class ProcessUtil {

    companion object {
        /**
         * 当前进程名是否为应用包名
         *
         * @return 返回true, 表示相同;返回false,表示不相同
         */
        fun isMainProcess(context: Context): Boolean {
            val pId = android.os.Process.myPid()
            logDebug("ProcessUtil-isMainProcess", "当前进程ID为:$pId")

            var processName: String? = null
            val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
            am?.let {
                val list = it.runningAppProcesses
                for (aList in list) {
                    val info = aList as ActivityManager.RunningAppProcessInfo
                    try {
                        if (info.pid == pId) {
                            processName = info.processName
                            logDebug("ProcessUtil-MainProcess", "当前进程名为:" + processName!!)
                        }
                    } catch (e: Exception) {
                        logError("ProcessUtil-MainProcess", "", e)
                    }

                }

                return processName != null && processName.equals(context.packageName, ignoreCase = true)
            }

            return false
        }
    }

}