package com.ly.module.network

import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.ly.module.util.GsonUtil
import com.ly.module.util.log.logDebug
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Lan Yang on 2020/8/23
 *
 * T 表明网络响应的数据需要通过gson转为实体类型
 *
 * TODO:还差进度未完成
 */
abstract class RequestBuilder<T> {

    private val tag = this.javaClass.simpleName

    private val typeToken by lazy {
        val type = this.javaClass.genericSuperclass
        val typeArray = (type as ParameterizedType).actualTypeArguments
        TypeToken.get(typeArray[0])
    }

    private val typeAdapter: TypeAdapter<T> by lazy {
        @Suppress("UNCHECKED_CAST")
        GsonUtil.gson.getAdapter(typeToken) as TypeAdapter<T>
    }

    private fun convertData(response: ResponseBody): T {
        val body = response.string()
        return typeAdapter.fromJson(body)
    }

    /**请求路径*/
    private var url: String? = null

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

    /**是否为下载文件的请求*/
    private var isDownload = false

    fun url(url: String) = apply {
        this.url = url
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
    fun uploadByteArray(byteArray: ByteArray, contentType: String) = apply {
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
    fun uploadByteArrayList(uploadByteArrayList: List<UploadByteArray>) = apply {
        this.contentType = REQUEST_BODY_TYPE_MULTIPART_FORM_DATA
        this.uploadByteArrayList = uploadByteArrayList
    }

    fun download() = apply {
        this.isDownload = true
    }

    suspend fun build(): T {
        return withContext(Dispatchers.IO) {

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
                                    formBuilder.add(pair.first.toString(), pair.second.toString())
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

                            requestBuilder.method(method, requestBody)
                        }

                        REQUEST_BODY_TYPE_UPLOAD -> {
                            when {
                                uploadFile != null -> {
                                    val file = uploadFile!!.first
                                    val contentType = uploadFile!!.second

                                    requestBuilder.method(
                                        method,
                                        file.asRequestBody(contentType.toMediaType())
                                    ).url(url!!)
                                }
                                uploadByteArray != null -> {
                                    val byteArray = uploadByteArray!!.first
                                    val contentType = uploadByteArray!!.second

                                    requestBuilder.method(
                                        method,
                                        byteArray.toRequestBody(contentType.toMediaType())
                                    )
                                }
                                else -> {
                                    throw IllegalArgumentException("构建请求失败，请检查！")
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
                                    val requestBody = file.asRequestBody(contentType.toMediaType())

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

                            requestBuilder.method(method, builder.build()).url(url!!)
                        }
                    }
                }
            }

            val request = requestBuilder.build()

            val okHttpClient = when {
                isDownload -> NetworkManager.getDownloadOkHttpClient()
                uploadFile != null -> NetworkManager.getUploadOkHttpClient()
                uploadByteArray != null -> NetworkManager.getUploadOkHttpClient()
                !uploadFileList.isNullOrEmpty() -> NetworkManager.getUploadOkHttpClient()
                !uploadByteArrayList.isNullOrEmpty() -> NetworkManager.getUploadOkHttpClient()
                else -> NetworkManager.okHttpClient
            }

            val call = okHttpClient.newCall(request)

            if (!isActive) {//检查协程是否被取消
                throw CancellationException("请求[${url}]被取消")
            }

            suspendCoroutine { continuation: Continuation<T> ->
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        logDebug(tag, "请求失败")
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        logDebug(tag, "请求成功")
                        if (!isActive) {//检查协程是否被取消
                            continuation.resumeWithException(CancellationException("请求[${url}]被取消"))
                        } else {
                            when (typeToken.rawType.name) {
                                Unit.toString() -> {
                                    @Suppress("UNCHECKED_CAST")
                                    continuation.resume(Unit as T)
                                }
                                Response::class.java.name -> {
                                    @Suppress("UNCHECKED_CAST")
                                    continuation.resume(response as T)
                                }
                                else -> {
                                    val value = if (response.body != null) {
                                        response.body!!.string()
                                    } else {
                                        ""
                                    }

                                    try {
                                        continuation.resume(typeAdapter.fromJson(value))
                                    } catch (e: Exception) {
                                        continuation.resumeWithException(e)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
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
        return Uri.encode(requestUrl)
    }
}