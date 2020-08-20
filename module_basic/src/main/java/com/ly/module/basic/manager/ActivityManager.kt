package com.ly.module.basic.manager

import android.app.Activity
import androidx.annotation.MainThread
import java.util.*

/**
 * Created by Lan Yang on 2020/8/20
 * 管理项目里已经启动的[Activity]
 */
object ActivityManager {

    private val list = LinkedList<Activity>()

    internal fun addActivity(activity: Activity) = list.add(activity)

    internal fun removeActivity(activity: Activity) = list.remove(activity)

    /**
     * 返回任务栈顶上的[Activity]
     */
    fun getTaskTopActivity(): Activity? {
        return if (list.isNotEmpty())
            return list.last()
        else
            null
    }

    /**
     * 在UI线程调用该方法，避免抛出[ConcurrentModificationException]异常
     */
    @MainThread
    fun finishAllActivity() {
        for (activity in list)
            activity.finish()
    }
}