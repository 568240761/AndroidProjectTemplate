package com.ly.module.network

import kotlinx.coroutines.CoroutineScope
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
    private val channel: Channel<Int>,
    private var path: String?
) : ResponseBody() {

    private val fileOutputStream: FileOutputStream? by lazy {
        if (path.isNullOrEmpty()) {
            null
        } else {
            val tempFile = File(path!!)
            val fileOutputStream = FileOutputStream(tempFile)
            fileOutputStream
        }
    }

    private val length = responseBody.contentLength().toDouble()

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

        override fun read(sink: Buffer, byteCount: Long): Long {
            val byteRead = super.read(sink, byteCount)

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

            coroutineScope.launch {
                val progress = (downloadLength.toDouble() / length * 100).toInt()
                channel.send(progress)

                if (progress == 100)
                    channel.close()
            }

            return byteRead
        }
    }
}