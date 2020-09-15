package com.ly.android

import com.ly.android.viewmodel.AppViewModel
import com.ly.module.network.RequestBuilder
import com.ly.module.util.log.logDebug

/**
 * Created by Lan Yang on 2020/9/15
 */
class TestNetworkViewModel() : AppViewModel() {

    fun test() {
        launch(
            block = {
                val params = HashMap<String, Any>()
                params["page"] = 1
                params["count"] = 2
                params["type"] = "video"

                val result = RequestBuilder().url("https://api.apiopen.top/getJoke")
                    .params(params)
                    .build()

                logDebug(tag, "test=${result.code()}")
            }
        )
    }

    fun downloadApk() {
        val path = AppApplication.app.getExternalFilesDir(null)!!.path + "/test.apk"
        launch(
            block = {
                val result =
                    RequestBuilder().url("https://cmt201910.oss-cn-zhangjiakou.aliyuncs.com/APP/hjk/test/huijiankang.apk")
                        .download(path)
                        .downloadProgress {
                            logDebug("MainViewModel", "progress=$it")
                        }
                        .build()

                logDebug(tag, "downloadApk=${result.code()}")
            },
            error = {
                logDebug("MainViewModel", "error")
            }
        )
    }
}