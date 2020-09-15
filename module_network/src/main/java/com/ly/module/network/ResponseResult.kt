package com.ly.module.network

import okhttp3.Response

/**
 * Created by Lan Yang on 2020/9/15
 *
 * 网络请求响应结果
 */
class ResponseResult(private val response: Response) {

    fun code() = response.code

    fun message() = response.message

    fun isSuccessful() = response.isSuccessful

    fun close() = response.close()

    fun source() = response.body?.source()

    /**
     * 以字符流形式返回响应。
     *
     * 响应字节默认解码为 UTF-8。
     */
    fun charStream() = response.body?.charStream()

    /**
     * 以流形式返回响应
     */
    fun byteStream() = response.body?.byteStream()

    /**
     * 以字节数组形式返回响应。
     *
     * 此方法将整个响应主体加载到内存中，如果响应主体很大，则此可能会触发[OutOfMemoryError]。
     */
    fun bytes() = response.body?.bytes()

    /**
     * 以字符串形式返回响应。
     *
     * 此方法将整个响应主体加载到内存中，如果响应主体很大，则此可能会触发[OutOfMemoryError]。
     *
     * 响应字节默认解码为 UTF-8。
     */
    fun string() = response.body?.string()

    /**
     * 以[okio.ByteString]形式返回响应。
     *
     * 此方法将整个响应主体加载到内存中，如果响应主体很大，则此可能会触发[OutOfMemoryError]。
     */
    fun byteString() = response.body?.byteString()

    override fun toString(): String {
        return response.toString()
    }
}