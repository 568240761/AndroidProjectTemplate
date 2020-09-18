package com.ly.module.log

import android.util.Log

/**
 * Created by Lan Yang on 2020/8/20
 *
 * 打印日志相关的方法
 */

/**是否打印日志，默认为true；项目上线置为false*/
var isPrintLog = true

fun logDebug(tag: String, msg: String) {
    if (isPrintLog)
        Log.d(tag, msg)
}

fun logInfo(tag: String, msg: String) {
    if (isPrintLog)
        Log.i(tag, msg)
}

fun logWarm(tag: String, msg: String) {
    if (isPrintLog)
        Log.w(tag, msg)
}

fun logError(tag: String, msg: String) {
    if (isPrintLog)
        Log.e(tag, msg)
}

fun logError(tag: String, msg: String, tr: Throwable) {
    if (isPrintLog)
        Log.e(tag, msg, tr)
}