package com.ly.module.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.ly.module.log.logDebug
import com.ly.module.log.logError
import java.io.FileReader
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.NetworkInterface
import java.util.*

/**
 * Created by Lan Yang on 2020/8/20
 * 关于手机设备方面的工具类
 */
class DeviceUtil {
    companion object {

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
            logDebug(
                "DeviceUtil",
                "手机厂商:${Build.MANUFACTURER}"
            )
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
            logDebug(
                "DeviceUtil",
                "API版本:${Build.VERSION.SDK_INT}"
            )
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

        /**
         * 生成设配唯一ID
         *
         * Android 10 开始申请 READ_PHONE_STATE 权限已没有用了，
         * 应用必须拥有 READ_PRIVILEGED_PHONE_STATE 权限才能获取 IMEI 和序列号。
         * 在搭载Android 10 的设备上，如果你的应用没有该权限，将会发生下列情况：
         * 1、以Android Q为目标平台,则会抛出SecurityException;
         * 2、以Android 9(28)或更低版本为目标平台；如果你的应用有READ_PHONE_STATE权限，
         * TelephonyManager提供的API会直接返回null，Build#getSerial返回Build.UNKNOWN；
         * 没有READ_PHONE_STATE权限，则会抛出SecurityException。
         *
         * 所以针对不同的版本做不同处理：
         * Android 8+的设备使用 ANDROID_ID；
         * Android 8 以下的设备使用 IMEI 和 硬件Mac地址，之所以要使用硬件Mac地址是因为平板电脑没有IMEI。
         *
         * IMEI：
         * IMEI(International Mobile Equipment Identity)是国际移动设备身份码的缩写，
         * 国际移动装备辨识码，是由15位数字组成的”电子串号”，它与每台移动电话机一一对应，而且该码是全世界唯一的。
         * 每一只移动电话机在组装完成后都将被赋予一个全球唯一的一组号码，这个号码从生产到交付使用都将被制造生产的厂商所记录。
         * 通俗来讲就是标识当前设备(手机)全世界唯一，类似于个人身份证。
         *
         * MAC：
         * MAC（Media Access Control或者Medium Access Control）地址，意译为媒体访问控制，或称为物理地址、硬件地址，用来定义网络设备的位置。
         * 在OSI模型中，第三层网络层负责 IP地址，第二层数据链路层则负责 MAC地址。
         * 因此一个主机会有一个MAC地址，而每个网络位置会有一个专属于它的IP地址通俗来讲就是标识你当前使用我这个软件(功能)时的地址。
         * 最主要的是：在平板设备上，无法通过imei标示设备，我们会将mac地址作为用户的唯一标识。
         *
         * IMSI：
         * 国际移动用户识别码（IMSI：International Mobile Subscriber IdentificationNumber）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。其总长度不超过15位，同样使用0~9的数字。
         * 其中MCC是移动用户所属国家代号，占3位数字，中国的MCC规定为460；
         * MNC是移动网号码，由两位或者三位数字组成，中国移动的移动网络编码（MNC）为00；用于识别移动用户所归属的移动通信网；
         * MSIN是移动用户识别码，用以识别某一移动通信网中的移动用户。
         * 通俗来讲就是标识当前SIM卡(手机卡)唯一，同样类似于个人身份证。
         *
         * 使用该方法需要的权限：
         * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
         * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
         * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
         * 请手动的将权限粘贴至清单文件中
         *
         * 对于Android 6.0+的设备需要动态申请 READ_PHONE_STATE 权限
         */
        @SuppressLint("HardwareIds")
        fun createDeviceId(context: Context): String {

            var id:String

            /*
            Android 8.0 开始对 ANDROID_ID 做了隐私性相关的变更:
            对于应用在 Android8.0+ 设备上的应用，应用的apk秘钥、用户和设备具有唯一的 ANDROID_ID 值。
            在相同设备上运行但具有不同签署秘钥的应用获取到的 ANDROID_ID 也不会相同;
            只要签署秘钥相同，apk卸载或重新安装，ANDROID_ID 的值都不会发生变化;
            即使系统更新导致软件包签署密钥发生变化，ANDROID_ID 的值也不会变化。
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } else {
                id = getImei(context)
                if (id.isEmpty()) {
                    id = getWifiMac(context)
                }
            }


            //id为空的情况：
            //1、[android 6.0,android 8.0)区间的平板设备获取Wifi的Mac失败了。
            logDebug(
                "DeviceUtil-createDeviceId",
                "生成唯一设配ID=$id"
            )
            return id
        }

        /**
         * 获取IMEI
         *
         * 平板设备或 Android 10.0+设备会返回null；
         * Android 10.0+ 适备不推荐使用该方法。
         *
         * 使用该方法需要的权限：
         * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
         * 请手动的将权限粘贴至清单文件中
         *
         * 对于Android 6.0+的设备需要动态申请 READ_PHONE_STATE 权限
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getImei(context: Context): String {
            var imei = ""
            try {
                val telephonyManager =
                    ContextCompat.getSystemService(context, TelephonyManager::class.java)

                val temp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    telephonyManager?.imei
                else
                    telephonyManager?.deviceId

                imei = temp ?: ""
            } catch (e: Exception) {
                logError("DeviceUtil-getImei", "", e)
            }

            logDebug("DeviceUtil-getImei", imei)

            return imei
        }

        /**
         * 获取WIFI的Mac
         *
         * 从此Android 6.0 版本开始，对于使用 WLAN API 和 Bluetooth API 的应用，移除了对设备本地硬件标识符的编程访问权；
         * 现在 WifiInfo.getMacAddress() 和 BluetoothAdapter.getAddress() 方法都将返回 02:00:00:00:00:00 。
         *
         * 部分设备在WIFI、4G以及无网络的情况可以正常获取Mac；较多设备仅仅支持在WIFI打开的情况下可以获取。
         * 所以为了获取 Mac 需要打开关闭了的WIFI，但是从 Android 10 开始，不允许编程的方式去打开或关闭WIFI。
         * Android 10.0+ 适备不推荐使用该方法。
         *
         * 使用该方法需要的权限：
         * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
         * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
         * 请手动的将权限粘贴至清单文件中
         *
         * 这个方法参考了多个博文，主要是参考博文：https://blog.csdn.net/u012400885/article/details/53505597/
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getWifiMac(context: Context): String {
            var wifiMac = ""
            val wifiManager = ContextCompat.getSystemService(context, WifiManager::class.java)
            wifiManager?.let {
                var isWifiEnabled = false
                if (it.isWifiEnabled) {
                    isWifiEnabled = true
                    //从 Android 10 开始，不允许编程的方式去打开或关闭WIFI
                    it.isWifiEnabled = true
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var ir: InputStreamReader? = null
                    var input: LineNumberReader? = null

                    try {
                        val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ")
                        ir = InputStreamReader(pp.inputStream)
                        input = LineNumberReader(ir)

                        val tempMac = input.readLine()
                        if (!tempMac.isNullOrEmpty()) {
                            wifiMac = tempMac.trim()
                        } else {
                            var reader: FileReader? = null
                            try {
                                reader = FileReader("/sys/class/net/eth0/address")

                                val stringBuilder = StringBuilder()
                                val buffer = CharArray(4096)

                                var length = reader.read(buffer)
                                while (length >= 0) {
                                    stringBuilder.append(buffer, 0, length)
                                    length = reader.read(buffer)
                                }

                                wifiMac = stringBuilder.toString()
                                    .trim()
                                    .toUpperCase(Locale.getDefault())
                                    .substring(0, 17)
                            } catch (e: Exception) {
                                logError(
                                    "DeviceUtil-getImei",
                                    "开始使用兼容7.0的方法",
                                    e
                                )
                                wifiMac =
                                    getAndroid7WifiMac()
                            } finally {
                                reader?.close()
                            }
                        }
                    } catch (e: Exception) {
                        logError(
                            "DeviceUtil-getImei",
                            "Android 6.0 以上的版本未获取到Wifi的Mac",
                            e
                        )

                    } finally {
                        input?.close()
                        ir?.close()
                    }

                } else {
                    try {
                        val wifiInfo = it.connectionInfo
                        val mac = wifiInfo.macAddress
                        if (!TextUtils.isEmpty(mac)) {
                            wifiMac = mac
                        }
                    } catch (e: Exception) {
                        logError(
                            "DeviceUtil-getImei",
                            "Android 6.0 以下的版本未获取到Wifi的Mac",
                            e
                        )
                    }
                }

                //从 Android 10 开始，不允许编程的方式去打开或关闭WIFI
                if (isWifiEnabled)
                    it.isWifiEnabled = false
            }

            logDebug("DeviceUtil-getWifiMac", wifiMac)
            return wifiMac
        }

        private fun getAndroid7WifiMac(): String {
            var wifiMac = ""

            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (nif.name.toLowerCase(Locale.getDefault()) != "wlan0") continue

                    val macBytes = nif.hardwareAddress
                    macBytes?.also {
                        val sb = StringBuilder()
                        for (b in it) {
                            sb.append(String.format("%02X:", b))
                        }
                        if (sb.isNotEmpty()) {
                            sb.deleteCharAt(sb.length - 1)
                        }
                        wifiMac = sb.toString()
                    }
                }
            } catch (e: Exception) {
                logError(
                    "DeviceUtil-getAndroid7WifiMac",
                    "兼容 Android 7.0 的方法未获取到Wifi的Mac",
                    e
                )
            }

            return wifiMac
        }
    }
}