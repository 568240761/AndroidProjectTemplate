package com.ly.module.network

import com.ly.module.log.logDebug
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Created by Lan Yang on 2020/8/22
 *
 * 简单地实现管理cookie，具体需要根据你项目需求。
 */
class NetworkCookieJar : CookieJar {
    private val tag = this.javaClass.simpleName

    private lateinit var currentCookies: List<Cookie>

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return if (this::currentCookies.isInitialized) {
            logDebug(tag, "loadForRequest")
            currentCookies
        } else {
            ArrayList()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        currentCookies = cookies

        cookies.forEach {
            logDebug(
                tag,
                "saveFromResponse[" +
                        "name=${it.name} " +
                        "value=${it.value} " +
                        "expiresAt=${it.expiresAt} " +
                        "domain=${it.domain} " +
                        "path=${it.path} " +
                        "secure=${it.secure} " +
                        "httpOnly=${it.httpOnly} " +
                        "hostOnly=${it.hostOnly} " +
                        "persistent=${it.persistent} " +
                        "]"
            )
        }
    }
}