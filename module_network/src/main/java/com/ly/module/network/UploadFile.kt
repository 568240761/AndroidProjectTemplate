package com.ly.module.network

import java.io.File

/**
 * Created by Lan Yang on 2020/8/28
 *
 * 在 Content-Type:multipart/form-data 的请求中的上传文件信息实体类
 */
data class UploadFile(
    val key: String,
    val value: String? = null,
    val file: File,
    /**文件类型；jpg图片，其值为image/jpg*/
    val contentType: String
)