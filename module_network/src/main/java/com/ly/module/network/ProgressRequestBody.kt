package com.ly.module.network

import com.ly.module.util.log.logError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*

/**
 * Created by Lan Yang on 2020/8/29
 *
 * 使用该请求正文能获取到进度
 */
internal class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val coroutineScope: CoroutineScope,
    private val channel: Channel<Int>
) : RequestBody() {

    private val totalByteLength by lazy {
        contentLength().toDouble()
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        try {
            requestBody.contentLength()
        } catch (e: Exception) {
            logError("ProgressRequestBody", "", e)
        }
        return super.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        val byteForwardingSink = ByteForwardingSink(sink)
        val bufferSink = byteForwardingSink.buffer()
        requestBody.writeTo(bufferSink)
        bufferSink.flush()
        bufferSink.close()
    }

    inner class ByteForwardingSink(sink: Sink) : ForwardingSink(sink) {

        private var uploadLength = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            uploadLength += byteCount

            coroutineScope.launch {
                val length = (uploadLength.toDouble() / totalByteLength * 100).toInt()
                channel.send(length)

                if (length == 100)
                    channel.close()
            }
        }
    }
}