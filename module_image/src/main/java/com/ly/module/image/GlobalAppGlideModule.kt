package com.ly.module.image

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.ly.module.util.log.isPrintLog

/**
 * Created by Lan Yang on 2020/9/12
 * 用于生成 GlideApp 类
 */
@GlideModule
class GlobalAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //开发模式打印日志
        if (isPrintLog) builder.setLogLevel(Log.DEBUG)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}