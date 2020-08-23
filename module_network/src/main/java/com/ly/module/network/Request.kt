package com.ly.module.network

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.ly.module.util.GsonUtil
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType

/**
 * Created by Lan Yang on 2020/8/23
 *
 * 网络请求
 */
class Request<T>() {

    private val typeAdapter: TypeAdapter<T>

    init {
        val typeArray = this.javaClass.genericInterfaces
        val params = (typeArray[0] as ParameterizedType).actualTypeArguments

        typeAdapter = GsonUtil.gson.getAdapter(TypeToken.get(params[0])) as TypeAdapter<T>
    }

    private fun convertData(response: ResponseBody): T {
        val body = response.string()
        return typeAdapter.fromJson(body)
    }
}