package com.ly.module.file_provider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Created by Lan Yang on 2020/8/20
 * [FileProvider]相关的工具类
 */
@Suppress("KDocUnresolvedReference")
class FileProviderUtil {
    companion object {

        fun authority(context: Context) = "${context.packageName}.FileProvider"

        /**
         * 将应用内的[File]路径转换为[Uri]
         */
        fun fileToUri(context: Context, file: File): Uri {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    authority(context),
                    file
                )
            } else {
                Uri.fromFile(file)
            }
        }

        /**
         * 安装APK文件
         *
         * Android 8.0+更新应用时跳转到安装页面需要下面的权限加到清单文件中
         * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
         */
        fun installApk(context: Context, apk: File) {
            val install = Intent()
            install.action = Intent.ACTION_VIEW

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val fileUri = fileToUri(context, apk)

            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            install.setDataAndType(
                fileUri,
                "application/vnd.android.package-archive"
            )

            context.startActivity(install)
        }
    }
}