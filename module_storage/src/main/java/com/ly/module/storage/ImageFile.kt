package com.ly.module.storage

import android.net.Uri

/**
 * Created by Lan Yang on 2020/9/19
 * 图片文件信息实体类
 */
data class ImageFile(
    /**图片文件在设备上的唯一ID*/
    val id: Long,
    /**图片文件名称*/
    val displayName: String,
    /**图片文件Uri*/
    val contentUri: Uri,
    /**图片文件类型*/
    val mimeType: String,
    /**保存图片文件的文件夹名称*/
    val bucket: String
)