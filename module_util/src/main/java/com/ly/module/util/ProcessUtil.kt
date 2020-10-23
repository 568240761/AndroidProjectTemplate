package com.ly.module.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.ly.module.log.logError

/**
 * Created by Lan Yang on 2020/8/20
 * 进程方面的工具类
 */
class ProcessUtil {

    companion object {

        /**
         * 当前进程是否为主进程
         *
         * @return 返回true, 表示相同;返回false,表示不相同
         */
        fun isMainProcess(context: Context, mainProcessName: String): Boolean {
            val processName = getCurrentProcessName(context)
            return processName != null && processName == mainProcessName
        }

        /**
         * 获取当前进程名
         */
        fun getCurrentProcessName(context: Context): String? {
            var processName: String? = null

            processName = getCurrentProcessNameByApplication()
            if (!processName.isNullOrEmpty()) return processName

            processName = getCurrentProcessNameByActivityThread()
            if (!processName.isNullOrEmpty()) return processName

            processName = getCurrentProcessNameByActivityManager(context)
            return processName
        }

        private fun getCurrentProcessNameByApplication() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else {
            null
        }

        //TODO(待测试)
        //在 android 4.3.1上可通过ActivityThread.currentProcessName()获取进程名
        private fun getCurrentProcessNameByActivityThread(): String? {
            var processName: String? = null

            try {
                val activityThread = Class.forName("android.app.ActivityThread", false, Application::class.java.classLoader)
                val declaredMethod = activityThread.getDeclaredMethod("currentProcessName")
                declaredMethod.isAccessible = true
                processName = declaredMethod.invoke(null, null) as String
            } catch (e: Exception) {
                logError("ProcessUtil", "getCurrentProcessNameByActivityThread()", e)
            }

            return processName
        }


        private fun getCurrentProcessNameByActivityManager(context: Context): String? {
            val pId = android.os.Process.myPid()

            var processName: String? = null
            val am = ContextCompat.getSystemService(context, ActivityManager::class.java)

            am?.let {
                try {
                    //ActivityManager.getRunningAppProcesses() 有可能调用失败，返回null，也就是 AIDL 调用失败。
                    //ActivityManager.getRunningAppProcesses()调用失败是极低的概率。
                    //当你的APP用户量达到一定的数量级别时，一定会有用户遇到。
                    val list = it.runningAppProcesses
                    for (aList in list) {
                        val info = aList as ActivityManager.RunningAppProcessInfo

                        if (info.pid == pId) {
                            processName = info.processName
                        }
                    }
                } catch (e: Exception) {
                    logError(
                            "ProcessUtil",
                            "getCurrentProcessNameByActivityManager()",
                            e
                    )
                }

            }

            return processName
        }
    }

}