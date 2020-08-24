package com.ly.module.network

import androidx.annotation.StringDef
import kotlin.annotation.Retention

/**
 * Created by Lan Yang on 2020/8/24
 * 网络请求方式
 */
const val REQUEST_METHOD_GET = "GET"
const val REQUEST_METHOD_HEAD = "HEAD"
const val REQUEST_METHOD_POST = "POST"
const val REQUEST_METHOD_DELETE = "DELETE"
const val REQUEST_METHOD_PUT = "PUT"
const val REQUEST_METHOD_PATCH = "PATCH"

@StringDef(
    value = [
        REQUEST_METHOD_GET,
        REQUEST_METHOD_HEAD,
        REQUEST_METHOD_POST,
        REQUEST_METHOD_DELETE,
        REQUEST_METHOD_PUT,
        REQUEST_METHOD_PATCH
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestMethod