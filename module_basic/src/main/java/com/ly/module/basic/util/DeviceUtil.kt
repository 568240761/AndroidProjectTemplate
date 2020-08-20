package com.ly.module.basic.util

import android.os.Build
import com.ly.module.log.logDebug

/**
 * Created by Lan Yang on 2020/8/20
 * 关于手机设备方面的工具类
 */
class DeviceUtil {
    companion object{

        /**
         * 是否为小米手机
         */
        fun isXiaomi(): Boolean {
            val flag = Build.MANUFACTURER
            logDebug("DeviceUtil", "手机厂商:$flag")
            return flag == "Xiaomi"
        }

        /**
         * 是否为华为手机
         */
        fun isHuawei(): Boolean {
            val flag = Build.MANUFACTURER
            logDebug("DeviceUtil", "手机厂商:$flag")
            return flag == "HUAWEI"
        }

        /**
         * 是否为魅族手机
         */
        fun isMeizu(): Boolean {
            val flag = Build.MANUFACTURER
            logDebug("DeviceUtil", "手机厂商:$flag")
            return flag == "Meizu"
        }


        /**
         * 手机厂商
         */
        fun getManufacturer(): String {
            logDebug("DeviceUtil","手机厂商:${Build.MANUFACTURER}")
            return Build.MANUFACTURER
        }

        /**
         * 手机型号
         */
        fun getModel(): String {
            logDebug("DeviceUtil", "手机型号:${Build.MODEL}")
            return Build.MODEL
        }

        /**
         * 手机系统版本
         */
        fun getSDKVersion(): String {
            logDebug("DeviceUtil","API版本:${Build.VERSION.SDK_INT}")
            return when (Build.VERSION.SDK_INT) {
                14 -> "Android 4.0"
                15 -> "Android 4.0.3"
                16 -> "Android 4.1"
                17 -> "Android 4.2"
                18 -> "Android 4.3"
                19 -> "Android 4.4"
                20 -> "Android 4.4W"
                21 -> "Android 5.0"
                22 -> "Android 5.1"
                23 -> "Android 6.0"
                24 -> "Android 7.0"
                25 -> "Android 7.1.1"
                26 -> "Android 8.0"
                27 -> "Android 8.1"
                28 -> "Android 9.0"
                29 -> "Android 10.0"
                else -> Build.VERSION.SDK_INT.toString()
            }
        }

        //TODO:生成唯一的设备ID
    }
}