package com.ly.module.network

/**
 * Created by Lan Yang on 2020/8/28
 *
 * 在 Content-Type:multipart/form-data 的请求中的上传字节数组信息实体类
 */
data class UploadByteArray(
    val key: String,
    val value: String? = null,
    val byteArray: ByteArray,
    /**文件类型；jpg图片，其值为image/jpg*/
    val contentType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadByteArray

        if (key != other.key) return false
        if (value != other.value) return false
        if (!byteArray.contentEquals(other.byteArray)) return false
        if (contentType != other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}