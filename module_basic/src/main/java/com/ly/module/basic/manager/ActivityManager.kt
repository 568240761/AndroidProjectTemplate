package com.ly.module.basic.manager

import android.app.Activity

/**
 * Created by Lan Yang on 2020/8/20
 * 管理项目里已经启动的[Activity]
 */
object ActivityManager {

    private val list = ArrayList<Activity>()

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
     * 避免抛出[ConcurrentModificationException]异常
     */
    fun finishAllActivity() {
        val iterable = list.iterator()
        while (iterable.hasNext()) {
            val activity = iterable.next()
            iterable.remove()
            activity.finish()
        }
    }
}