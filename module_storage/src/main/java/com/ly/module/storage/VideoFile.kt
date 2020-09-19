package com.ly.module.storage

import android.net.Uri

/**
 * Created by Lan Yang on 2020/9/19
 * 视频文件信息实体类
 */
class VideoFile(
    /**视频文件在设备上的唯一ID*/
    val id: Long,
    /**视频文件名称*/
    val displayName: String,
    /**视频文件Uri*/
    val contentUri: Uri,
    /**视频文件类型*/
    val mimeType: String,
    /**保存视频文件的文件夹名称*/
    val bucket: String
)