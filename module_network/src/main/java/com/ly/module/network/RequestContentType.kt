package com.ly.module.network

import androidx.annotation.IntDef

/**
 * Created by Lan Yang on 2020/8/24
 * 请求内容类型
 */

/**get请求是没有请求内容的，请求参数是拼接在url后面的*/
const val REQUEST_CONTENT_TYPE_NULL = 0

/**表单;Content-Type:application/x-www-form-urlencoded*/
const val REQUEST_CONTENT_TYPE_FORM = REQUEST_CONTENT_TYPE_NULL + 1

/**JSON;Content-Type:application/json; charset=utf-8*/
const val REQUEST_CONTENT_TYPE_JSON = REQUEST_CONTENT_TYPE_FORM + 1

/**Content-Type:multipart/form-data*/
const val REQUEST_CONTENT_TYPE_MULTIPART_FORM_DATA = REQUEST_CONTENT_TYPE_JSON + 1

@IntDef(
    flag = true,
    value = [
        REQUEST_CONTENT_TYPE_NULL,
        REQUEST_CONTENT_TYPE_FORM,
        REQUEST_CONTENT_TYPE_JSON,
        REQUEST_CONTENT_TYPE_MULTIPART_FORM_DATA
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestContentType