package com.ly.module.basic.handler

import android.os.Process
import com.ly.module.basic.manager.ActivityManager
import com.ly.module.log.logError
import kotlin.system.exitProcess

/**
 * Created by Lan Yang on 2020/9/6
 *
 * 在开发过程中，应用发生Crash时异常退出，然后又自动启动跳转到未知页面。
 * 此时应用在崩溃前保存的全局变量被重置，用户状态丢失，显示数据错乱。
 * 这种崩溃后重启的情况，并不是每次都会遇到。
 *
 * 在 Android 的 API 21 ( Android 5.0 ) 以下，Crash 会直接退出应用。
 * 在 API 21 ( Android 5.0 ) 以上，系统会遵循以下原则进行重启：
 * 1、包含 Service，如果应用 Crash 的时候，运行着Service，那么系统会重新启动 Service；
 * 2、不包含 Service，只有一个 Activity，那么系统不会重新启动该 Activity；
 * 3、不包含 Service，但当前堆栈中存在两个 Activity：Act1 -> Act2，如果 Act2 发生了 Crash ，那么系统会重启 Act1；
 * 4、不包含 Service，但是当前堆栈中存在三个 Activity：Act1 -> Act2 -> Act3，如果 Act3 崩溃，那么系统会重启 Act2，并且 Act1 依然存在，即可以从重启的 Act2 回到 Act1。
 *
 * 面对这样的问题，提供两种解决思路：
 * 1、允许应用自动重启，并在重启时恢复应用在崩溃前的运行状态；
 * 2、禁止应用自动重启，而是让用户在应用发生崩溃后自己手动重启应用。
 *
 * 该类就是使用的第二种解决思路。
 */
object CloseProcessHandler : Thread.UncaughtExceptionHandler {

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        logError(this.javaClass.simpleName, "", e)

        //捕获到未处理的异常，退出应用
        ActivityManager.finishAllActivity()
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}