package com.ly.module.network

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.ly.module.util.GsonUtil
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType

/**
 * Created by Lan Yang on 2020/8/23
 *
 * T 表明网络响应的数据需要通过gson转为实体类型
 */
abstract class RequestBuilder<T>() {

    private val typeAdapter: TypeAdapter<T> by lazy {
        val type = this.javaClass.genericSuperclass
        val params = (type as ParameterizedType).actualTypeArguments
        GsonUtil.gson.getAdapter(TypeToken.get(params[0])) as TypeAdapter<T>
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

    /**请求内容类型*/
    @RequestContentType
    private var contentType: Int = REQUEST_CONTENT_TYPE_NULL

    /**请求方式*/
    @RequestMethod
    private var method: String = REQUEST_METHOD_GET

    fun url(url: String) = apply {
        this.url = url
    }

    fun params(params: Any) = apply {
        this.params = params
    }

    fun headers(headers: Map<String, Any>) = apply {
        this.headers = headers
    }

    fun contentType(@RequestContentType contentType: Int) = apply {
        this.contentType = contentType
    }

    fun method(@RequestMethod method: String) = apply {
        this.method = method
    }
}