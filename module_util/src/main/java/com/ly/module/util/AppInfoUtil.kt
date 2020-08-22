package com.ly.module.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.ly.module.util.log.isPrintLog
import com.ly.module.util.log.logDebug

/**
 * Created by Lan Yang on 2020/8/22
 *
 * 获取应用信息的工具类
 */
class AppInfoUtil {
    companion object {

        /**
         * 获取应用签名私钥的证书指纹SHA-1
         */
        @SuppressLint("PackageManagerGetSignatures")
        fun getSignatureInfo(context: Context): String {
            var sha1 = ""
            val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )

                val signingInfo = packageInfo.signingInfo
                //判断包是否由多个签名者签名，默认一个签名者签名
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }

            } else {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
                packageInfo.signatures
            }

            signature?.also {

                logDebug("AppInfoUtil-getSignatureInfo", "数量=${it.size}")

                if (signature.isNotEmpty()) {
                    if (isPrintLog)
                        for (sign in it) {
                            sign?.let {
                                logDebug("AppInfoUtil-getSignatureInfo", sign.toCharsString())
                            }
                        }

                    //默认取第一个
                    val sign = signature[0]

                    sha1 = MessageDigestUtil.getSHAMessageDigest(sign.toByteArray())
                    logDebug("AppInfoUtil-getSignatureInfo", "SHA-1=${sha1}")
                }
            }

            return sha1
        }
    }
}