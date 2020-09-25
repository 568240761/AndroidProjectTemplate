package com.ly.module.storage

import android.net.Uri

/**
 * Created by Lan Yang on 2020/9/18
 * 文件信息实体类
 */
data class StorageFile(
    /**文件在设备上的唯一ID*/
    val id: Long,
    /**文件名称*/
    val displayName: String,
    /**文件Uri*/
    val contentUri: Uri,
    /**文件类型*/
    val mimeType: String
)