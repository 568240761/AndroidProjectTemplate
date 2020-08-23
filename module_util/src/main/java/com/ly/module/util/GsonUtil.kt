package com.ly.module.util

import com.google.gson.Gson

/**
 * Created by Lan Yang on 2020/8/23
 * gson相关的工具类
 */
class GsonUtil {
    companion object {

        val gson = Gson()

        /**
         *将JSON字符串转化为实体类
         * @param str JSON字符串
         * @param clazz [T]类信息
         * @return [T]实体类
         */
        fun <T> fromJson(str: String, clazz: Class<T>): T {
            return gson.fromJson(str, clazz)
        }

        /**
         *将实体类转化为JSON字符串
         * @return JSON字符串
         */
        fun toJson(any: Any): String {
            return gson.toJson(any)
        }
    }
}