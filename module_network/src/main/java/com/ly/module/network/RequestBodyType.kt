package com.ly.module.network

import androidx.annotation.IntDef

/**
 * Created by Lan Yang on 2020/8/24
 * 请求正文类型
 */

/**get请求是没有请求正文的，请求参数是拼接在url后面的*/
const val REQUEST_BODY_TYPE_NULL = 0

/**表单，请求主体是键值对形式的字符串；Content-Type:application/x-www-form-urlencoded*/
const val REQUEST_BODY_TYPE_FORM = REQUEST_BODY_TYPE_NULL + 1

/**JSON，请求主体是json形式的字符串；Content-Type:application/json; charset=utf-8*/
const val REQUEST_BODY_TYPE_JSON = REQUEST_BODY_TYPE_FORM + 1

/**
 * 主要用于构建上传文件的请求主体，一次只能上传一个文件；
 * Content-Type需要根据的上传文件类型确定，比如上传jpg图片，Content-Type:image/jpg
 */
const val REQUEST_BODY_TYPE_UPLOAD = REQUEST_BODY_TYPE_JSON + 1

/**构建复杂请求主体,比如可以同时可以上传多个文件；Content-Type:multipart/form-data*/
const val REQUEST_BODY_TYPE_MULTIPART_FORM_DATA = REQUEST_BODY_TYPE_UPLOAD + 1

@IntDef(
    value = [
        REQUEST_BODY_TYPE_NULL,
        REQUEST_BODY_TYPE_FORM,
        REQUEST_BODY_TYPE_JSON,
        REQUEST_BODY_TYPE_UPLOAD,
        REQUEST_BODY_TYPE_MULTIPART_FORM_DATA
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestBodyType