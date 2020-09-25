package com.ly.module.network

import com.ly.module.log.logDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Lan Yang on 2020/8/30
 * 使用该响应正文能获取到进度
 */
class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val coroutineScope: CoroutineScope,
    private val channel: Channel<Int>?,
    private var path: String?
) : ResponseBody() {

    private val tag = this::class.java.simpleName

    private val fileOutputStream: FileOutputStream? by lazy {
        if (path.isNullOrEmpty()) {
            null
        } else {
            //该文件只能存放应用专属目里，否则在Android 10开始会报错。
            val tempFile = File(path!!)
            val fileOutputStream = FileOutputStream(tempFile)
            fileOutputStream
        }
    }

    private val length = responseBody.contentLength()

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        return ByteForwardingSource(responseBody.source()).buffer()
    }

    inner class ByteForwardingSource(source: Source) : ForwardingSource(source) {
        private var downloadLength = 0L
        private var prevProgress = -1

        override fun read(sink: Buffer, byteCount: Long): Long {
            val byteRead: Long

            try {
                byteRead = super.read(sink, byteCount)

                downloadLength += if (byteRead != -1L) {
                    fileOutputStream?.also {
                        sink.writeTo(it)
                    }

                    byteRead
                } else {
                    fileOutputStream?.flush()
                    fileOutputStream?.close()

                    0
                }
            } catch (e: Exception) {
                fileOutputStream?.flush()
                fileOutputStream?.close()
                throw e
            }

            channel?.also {
                val progress = (downloadLength.toDouble() / length * 100).toInt()

                if (prevProgress != progress) {//避免重复发送相同的进度值
                    if (progress == 100 && downloadLength == length) {
                        sendProgress(progress, channel)
                    } else {
                        sendProgress(progress, channel)
                    }
                }
            }

            return byteRead
        }

        private fun sendProgress(progress: Int, channel: Channel<Int>) {
            prevProgress = progress
            logDebug(tag, "progress = $progress")
            coroutineScope.launch(Dispatchers.IO) {
                channel.send(progress)

                if (progress == 100)
                    channel.close()
            }
        }
    }
}