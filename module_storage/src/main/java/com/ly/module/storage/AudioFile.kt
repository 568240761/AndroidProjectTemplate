package com.ly.module.storage

import android.net.Uri

/**
 * Created by Lan Yang on 2020/9/19
 * 音频文件信息实体类
 */
class AudioFile(
    /**音频文件在设备上的唯一ID*/
    val id: Long,
    /**音频文件名称*/
    val displayName: String,
    /**音频文件Uri*/
    val contentUri: Uri,
    /**音频文件类型*/
    val mimeType: String
)
