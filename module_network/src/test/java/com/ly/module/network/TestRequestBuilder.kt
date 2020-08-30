package com.ly.module.network

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.ly.module.util.GsonUtil
import okhttp3.Response
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType

/**
 * Created by Lan Yang on 2020/8/23
 *
 * T 表明网络响应的数据需要通过gson转为实体类型
 */
abstract class TestRequestBuilder<T> {

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

    private fun test() {
        when (typeToken.rawType.name) {
            Unit.toString() -> {
                println(typeToken.rawType.name)
            }
            Response::class.java.name -> {
                println(typeToken.rawType.name)
            }
        }
    }
}