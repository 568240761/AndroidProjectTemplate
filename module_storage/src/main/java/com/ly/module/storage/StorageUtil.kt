package com.ly.module.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Lan Yang on 2020/9/18
 */
class StorageUtil {
    companion object {

        /**查询图片*/
        suspend fun queryImages(): List<StorageFile> {
            val images = mutableListOf<StorageFile>()
            withContext(Dispatchers.IO) {

            }
            return images
        }

        /**查询视频*/
        suspend fun queryVideos(): List<StorageFile> {
            val videos = mutableListOf<StorageFile>()
            withContext(Dispatchers.IO) {

            }
            return videos
        }

        /**查询音频*/
        suspend fun queryAudios(): List<StorageFile> {
            val audios = mutableListOf<StorageFile>()
            withContext(Dispatchers.IO) {

            }
            return audios
        }
    }
}