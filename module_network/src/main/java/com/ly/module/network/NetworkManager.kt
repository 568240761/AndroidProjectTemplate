package com.ly.module.network

import com.ly.module.util.log.isPrintLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by Lan Yang on 2020/8/22
 *
 * 网络管理
 */
object NetworkManager {

    private val tag = this.javaClass.simpleName

    /**默认的超时时间为 5s*/
    private const val DEFAULT_TIMEOUT = 5L

    /**设置全局固定的请求头*/
    private val globalFixedHeaders = HashMap<String, Any>()

    private val okHttpClientBuilder = OkHttpClient.Builder()
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)

    val okHttpClient: OkHttpClient

    init {
        //开发模式，打印网络日志
        if (isPrintLog) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        //管理cookie
        okHttpClientBuilder.cookieJar(NetworkCookieJar())

        okHttpClient = okHttpClientBuilder.build()
    }
}