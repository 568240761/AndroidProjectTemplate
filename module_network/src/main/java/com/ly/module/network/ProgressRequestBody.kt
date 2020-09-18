package com.ly.module.network

import com.ly.module.log.logDebug
import com.ly.module.log.logError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val tag = this::class.java.simpleName

    private val totalByteLength by lazy {
        contentLength()
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
        private var prevProgress = -1

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            uploadLength += byteCount

            val progress = (uploadLength.toDouble() / totalByteLength * 100).toInt()

            if (prevProgress != progress) {//避免重复发送相同的进度值
                if (progress == 100 && uploadLength == totalByteLength) {
                    sendProgress(progress)
                } else {
                    sendProgress(progress)
                }
            }
        }

        private fun sendProgress(progress: Int) {
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