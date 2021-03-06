package com.ly.module.network

import com.ly.module.util.GsonUtil
import com.ly.module.log.logDebug
import com.ly.module.log.logError
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import kotlin.IllegalStateException
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Lan Yang on 2020/8/23
 *
 * 网络请求的构建
 * TODO:上传进度、上传文件待测试
 */
class RequestBuilder {

    private val tag = this.javaClass.simpleName

    companion object {
        const val DEFAULT_MAX_TIMEOUT = 5 * 60 * 1000L

        private val urlSet = HashSet<String>()

        private fun addUrl(str: String): Boolean {
            val flag = urlSet.add(str)

            if (flag) {
                logDebug("RequestBuilder_addUrl", "记录[$str]成功")
            }

            return flag
        }

        private fun removeUrl(str: String): Boolean {
            val flag = urlSet.remove(str)

            if (flag) {
                logDebug("RequestBuilder_removeUrl", "移除[$str]成功")
            } else {
                logDebug("RequestBuilder_removeUrl", "未找到[$str]")
            }

            return flag
        }
    }

    private lateinit var call: Call

    /**请求路径*/
    private var url: String? = null

    /**是否记录请求，默认记录*/
    private var isRecordUrl: Boolean = true

    /**请求参数*/
    private var params: Any? = null

    /**请求头*/
    private var headers: Map<String, Any>? = null

    /**请求内容类型；默认Content-Type:application/json; charset=utf-8*/
    @RequestBodyType
    private var contentType: Int = REQUEST_BODY_TYPE_JSON

    /**请求方式；默认get请求*/
    @RequestMethod
    private var method: String = REQUEST_METHOD_GET

    /**上传单个文件*/
    private var uploadFile: Pair<File, String>? = null

    /**上传单个字节数组*/
    private var uploadByteArray: Pair<ByteArray, String>? = null

    /**上传多个文件*/
    private var uploadFileList: List<UploadFile>? = null

    /**上传多个字节数组*/
    private var uploadByteArrayList: List<UploadByteArray>? = null

    /**文件下载后存放地址，为空表示不在本地存放文件*/
    private var downloadPath: String? = null

    /**是否为下载文件的请求*/
    private var isDownload = false

    /**是否显示上传的进度*/
    private var isUploadProgress = false

    /**上传进度方法回调*/
    private var uploadProgress: (progress: Int) -> Unit = {}

    /**是否显示下载的进度*/
    private var isDownloadProgress = false

    /**下载进度方法回调*/
    private var downloadProgress: (progress: Int) -> Unit = {}

    /**未设置超时时间，使用默认超时时间；单位为秒*/
    private var timeout = -1L

    /**在协程之间传输数值*/
    private val uploadChannel by lazy {
        Channel<Int>()
    }

    /**在协程之间传输数值*/
    private val downloadChannel by lazy {
        Channel<Int>()
    }

    fun url(url: String) = apply {
        this.url = url
    }

    fun recordUrl(isRecordUrl: Boolean) = apply {
        this.isRecordUrl = isRecordUrl
    }

    fun params(params: Any) = apply {
        this.params = params
    }

    fun headers(headers: Map<String, Any>) = apply {
        this.headers = headers
    }

    /**
     * [REQUEST_METHOD_GET]、[REQUEST_METHOD_HEAD]不需要设置[contentType]；
     * [REQUEST_METHOD_DELETE]、[REQUEST_METHOD_POST]、[REQUEST_METHOD_PUT]、[REQUEST_METHOD_PATCH]需要设置[contentType]；
     * 不设置的话，会使用默认的[contentType]。
     */
    fun contentType(@RequestBodyType contentType: Int) = apply {
        this.contentType = contentType
    }

    fun method(@RequestMethod method: String) = apply {
        this.method = method
    }

    fun post() = apply {
        this.method = REQUEST_METHOD_POST
    }

    fun postForm() = apply {
        this.contentType = REQUEST_BODY_TYPE_FORM
        this.method = REQUEST_METHOD_POST
    }

    /**
     * 上传单个文件[file]，[contentType]表示上传类型；
     * 比如上传jpg图片，[contentType]可写为image/jpg。
     */
    fun uploadFile(file: File, contentType: String) = apply {
        this.contentType = REQUEST_BODY_TYPE_UPLOAD
        this.uploadFile = Pair(file, contentType)
    }

    /**
     * 上传单个字节数组[byteArray]，[contentType]表示上传类型；
     * 比如上传jpg图片，[contentType]可写为image/jpg。
     */
    fun uploadByteArray(
        byteArray: ByteArray,
        contentType: String
    ) = apply {
        this.contentType = REQUEST_BODY_TYPE_UPLOAD
        this.uploadByteArray = Pair(byteArray, contentType)
    }

    /**
     * 上传多个文件
     */
    fun uploadFileList(uploadFileList: List<UploadFile>) = apply {
        this.contentType = REQUEST_BODY_TYPE_MULTIPART_FORM_DATA
        this.uploadFileList = uploadFileList
    }

    /**
     * 上传多个字节数组
     */
    fun uploadByteArrayList(
        uploadByteArrayList: List<UploadByteArray>
    ) = apply {
        this.contentType = REQUEST_BODY_TYPE_MULTIPART_FORM_DATA
        this.uploadByteArrayList = uploadByteArrayList
    }

    /**
     * 显示上传进度
     */
    fun uploadProgress(progress: (progress: Int) -> Unit) = apply {
        this.isUploadProgress = true
        this.uploadProgress = progress
    }

    /**
     * 下载文件，存放至[downloadPath]
     *
     * @param downloadPath 为空表示不在本地存放文件
     */
    fun download(downloadPath: String? = null) = apply {
        this.isDownload = true
        this.downloadPath = downloadPath
    }

    /**
     * 显示下载进度
     */
    fun downloadProgress(progress: (progress: Int) -> Unit) = apply {
        this.isDownloadProgress = true
        this.downloadProgress = progress
    }

    /**
     * 设置超时[timeout]
     */
    fun timeout(timeout: Long) = apply {
        this.timeout = timeout
    }

    suspend fun build(): ResponseResult {

        if (url.isNullOrEmpty())
            throw IllegalArgumentException("请求路径不能为空")

        if (isRecordUrl && !addUrl(url!!))
            throw IllegalStateException("存在相同的[$url]正在执行")

        val result: ResponseResult
        try {
            result = coroutineScope {

                val requestBuilder = createRequestHeader()

                when (method) {
                    REQUEST_METHOD_GET,
                    REQUEST_METHOD_HEAD -> {
                        requestBuilder.method(method, null).url(appendUrlParams())
                    }
                    REQUEST_METHOD_POST,
                    REQUEST_METHOD_DELETE,
                    REQUEST_METHOD_PATCH,
                    REQUEST_METHOD_PUT -> {
                        when (contentType) {
                            REQUEST_BODY_TYPE_NULL -> {
                                //请求正文为空时，POST、PATCH、PUT会抛出异常，DELETE不会抛出异常
                                requestBuilder.method(method, null).url(url!!)
                            }

                            REQUEST_BODY_TYPE_FORM -> {
                                val formBuilder = FormBody.Builder()

                                if (params != null && params is Map<*, *>) {
                                    val list = (params!! as Map<*, *>).toList()
                                    for (pair in list) {
                                        formBuilder.add(
                                            pair.first.toString(),
                                            pair.second.toString()
                                        )
                                    }
                                }

                                requestBuilder.method(method, formBuilder.build()).url(url!!)
                            }

                            REQUEST_BODY_TYPE_JSON -> {
                                val content = if (params == null)
                                    ""
                                else
                                    GsonUtil.toJson(params!!)

                                val requestBody =
                                    content.toRequestBody("application/json; charset=utf-8".toMediaType())

                                requestBuilder.method(method, requestBody).url(url!!)
                            }

                            REQUEST_BODY_TYPE_UPLOAD -> {
                                when {
                                    uploadFile != null -> {
                                        val file = uploadFile!!.first
                                        val contentType = uploadFile!!.second

                                        val requestBody = createUploadRequestBody(
                                            coroutineScope = this,
                                            requestBody = file.asRequestBody(contentType.toMediaType())
                                        )

                                        requestBuilder.method(method, requestBody).url(url!!)
                                    }
                                    uploadByteArray != null -> {
                                        val byteArray = uploadByteArray!!.first
                                        val contentType = uploadByteArray!!.second

                                        val requestBody = createUploadRequestBody(
                                            coroutineScope = this,
                                            requestBody = byteArray.toRequestBody(contentType.toMediaType())
                                        )

                                        requestBuilder.method(method, requestBody).url(url!!)
                                    }
                                    else -> {
                                        throw IllegalArgumentException("构建请求失败，请检查构建参数！")
                                    }
                                }
                            }

                            REQUEST_BODY_TYPE_MULTIPART_FORM_DATA -> {
                                val builder = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)

                                if (params != null && params is Map<*, *>) {
                                    val list = (params!! as Map<*, *>).toList()
                                    for (pair in list) {
                                        builder.addFormDataPart(
                                            pair.first.toString(),
                                            pair.second.toString()
                                        )
                                    }
                                }

                                if (!uploadFileList.isNullOrEmpty()) {
                                    uploadFileList!!.forEach {
                                        val file = it.file
                                        val contentType = it.contentType
                                        val requestBody =
                                            file.asRequestBody(contentType.toMediaType())

                                        builder.addFormDataPart(it.key, it.value, requestBody)
                                    }
                                }

                                if (!uploadByteArrayList.isNullOrEmpty()) {
                                    uploadByteArrayList!!.forEach {
                                        val byteArray = it.byteArray
                                        val contentType = it.contentType
                                        val requestBody =
                                            byteArray.toRequestBody(contentType.toMediaType())

                                        builder.addFormDataPart(it.key, it.value, requestBody)
                                    }
                                }

                                val requestBody = createUploadRequestBody(
                                    coroutineScope = this,
                                    requestBody = builder.build()
                                )

                                requestBuilder.method(method, requestBody).url(url!!)
                            }
                        }
                    }
                }

                val request = requestBuilder.build()

                val okHttpClient = getOkHttpClient(this)

                call = okHttpClient.newCall(request)

                if (isDownloadProgress) {
                    launch(Dispatchers.IO) {
                        for (progress in downloadChannel) {
                            withContext(Dispatchers.Main) {
                                downloadProgress(progress)
                            }
                        }
                    }
                }

                if (isUploadProgress) {
                    launch(Dispatchers.IO) {
                        for (progress in uploadChannel) {
                            withContext(Dispatchers.Main) {
                                uploadProgress(progress)
                            }
                        }
                    }
                }

                suspendCancellableCoroutine { continuation: CancellableContinuation<ResponseResult> ->
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            logError(tag, "请求[$url]失败", e)
                            deleteTempFile()

                            continuation.resumeWithException(e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            logDebug(tag, "请求[$url]成功")

                            if (!continuation.isActive) {//检查协程是否被取消
                                logDebug(tag, "协程被取消")
                            } else {
                                continuation.resume(ResponseResult(response))
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException && this::call.isInitialized) {//如果协程被取消，则取消网络请求
                call.cancel()
            }

            //往上层抛出异常，是为将异常交给上层处理
            throw e
        } finally {
            if (isRecordUrl)
                removeUrl(url!!)
        }

        return result
    }

    private fun createRequestHeader(): Request.Builder {
        val requestBuilder = Request.Builder()

        if (NetworkManager.globalFixedHeaders.isNotEmpty()) {
            val list = NetworkManager.globalFixedHeaders.toList()
            for (pair in list) {
                requestBuilder.header(pair.first, pair.second.toString())
            }
        }

        if (!headers.isNullOrEmpty()) {
            val list = headers!!.toList()
            for (pair in list) {
                requestBuilder.header(pair.first, pair.second.toString())
            }
        }

        return requestBuilder
    }

    private fun appendUrlParams(): String {
        if (url.isNullOrEmpty())
            throw IllegalArgumentException("请求路径不能为空")

        var requestUrl = url!!
        if (params != null && params is HashMap<*, *>) {
            val paramsHashMap = params as HashMap<*, *>
            if (paramsHashMap.isNotEmpty()) {
                val sb = StringBuilder(requestUrl).append("?")
                for ((index, pair) in paramsHashMap.toList().withIndex()) {
                    sb.append(pair.first.toString()).append("=").append(pair.second.toString())
                    if (index != paramsHashMap.size - 1)
                        sb.append("&")
                }

                requestUrl = sb.toString()
            }
        }
        return requestUrl
    }

    private fun createUploadRequestBody(
        coroutineScope: CoroutineScope,
        requestBody: RequestBody
    ): RequestBody {
        return if (isUploadProgress) {
            ProgressRequestBody(
                requestBody = requestBody,
                coroutineScope = coroutineScope,
                channel = uploadChannel
            )
        } else {
            requestBody
        }
    }

    private fun getOkHttpClient(coroutineScope: CoroutineScope): OkHttpClient {
        return when {
            isDownload -> {
                val builder =
                    NetworkManager.getOkHttpClientBuilder(if (timeout > 0) timeout else DEFAULT_MAX_TIMEOUT)

                //如果既不需要保存文件到本地，又不需要显示下载进度，就不需要使用 addNetworkInterceptor() 方法
                if (!downloadPath.isNullOrEmpty() || isDownloadProgress) {
                    builder.addNetworkInterceptor { chain ->
                        val response = chain.proceed(chain.request())
                        response.newBuilder()
                            .body(
                                ProgressResponseBody(
                                    responseBody = response.body!!,
                                    coroutineScope = coroutineScope,
                                    channel = if (isDownloadProgress) downloadChannel else null,
                                    path = downloadPath
                                )
                            )
                            .build()
                    }
                }

                builder.build()
            }
            uploadFile != null -> {
                NetworkManager.getOkHttpClientBuilder(if (timeout > 0) timeout else DEFAULT_MAX_TIMEOUT)
                    .build()
            }
            uploadByteArray != null -> {
                NetworkManager.getOkHttpClientBuilder(if (timeout > 0) timeout else DEFAULT_MAX_TIMEOUT)
                    .build()
            }
            !uploadFileList.isNullOrEmpty() -> {
                NetworkManager.getOkHttpClientBuilder(if (timeout > 0) timeout else DEFAULT_MAX_TIMEOUT)
                    .build()
            }
            !uploadByteArrayList.isNullOrEmpty() -> {
                NetworkManager.getOkHttpClientBuilder(if (timeout > 0) timeout else DEFAULT_MAX_TIMEOUT)
                    .build()
            }
            else -> {
                if (timeout > 0)
                    NetworkManager.getOkHttpClientBuilder(timeout).build()
                else
                    NetworkManager.okHttpClient
            }
        }
    }

    /**
     * 下载失败，删除正在下载的文件
     */
    private fun deleteTempFile() {
        if (isDownload && !downloadPath.isNullOrEmpty()) {
            val file = File(downloadPath!!)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}