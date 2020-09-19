package com.ly.module.storage

import android.app.Application
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Lan Yang on 2020/9/18
 */
class StorageUtil {
    companion object {

        /**Android 10+ 开始，该字段从[MediaStore.Images.ImageColumns]、[MediaStore.Video.VideoColumns]移入[MediaStore.MediaColumns]中*/
        private const val BUCKET_DISPLAY_NAME = "bucket_display_name"

        /**查询外部存储空间上的图片*/
        suspend fun queryImages(application: Application): List<ImageFile> {
            val images = mutableListOf<ImageFile>()
            withContext(Dispatchers.IO) {
                val projection =
                    arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.MIME_TYPE,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                        } else {
                            BUCKET_DISPLAY_NAME
                        }
                    )

                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

                application.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                    val bucketColumn =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                        } else {
                            cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)
                        }

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        val bucket = cursor.getString(bucketColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val imageFile = ImageFile(
                            id = id,
                            displayName = displayName,
                            mimeType = mimeType,
                            bucket = bucket,
                            contentUri = contentUri
                        )

                        images.add(imageFile)
                    }
                }
            }
            return images
        }

        /**查询外部存储空间上的视频*/
        suspend fun queryVideos(application: Application): List<VideoFile> {
            val videos = mutableListOf<VideoFile>()
            withContext(Dispatchers.IO) {
                val projection =
                    arrayOf(
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.MIME_TYPE,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                        } else {
                            BUCKET_DISPLAY_NAME
                        }
                    )


                val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

                application.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                    val bucketColumn =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                        } else {
                            cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)
                        }

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        val bucket = cursor.getString(bucketColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val videoFile = VideoFile(
                            id = id,
                            displayName = displayName,
                            mimeType = mimeType,
                            bucket = bucket,
                            contentUri = contentUri
                        )

                        videos.add(videoFile)
                    }
                }
            }
            return videos
        }

        /**查询外部存储空间上的音频*/
        suspend fun queryAudios(application: Application): List<AudioFile> {
            val audios = mutableListOf<AudioFile>()
            withContext(Dispatchers.IO) {
                val projection =
                    arrayOf(
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.MIME_TYPE
                    )

                val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

                application.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val audioFile = AudioFile(
                            id = id,
                            displayName = displayName,
                            mimeType = mimeType,
                            contentUri = contentUri
                        )

                        audios.add(audioFile)
                    }
                }
            }
            return audios
        }

        //TODO:未完成
        suspend fun queryImagesAndVideos(application: Application): List<StorageFile> {
            val storages = mutableListOf<StorageFile>()

            withContext(Dispatchers.IO) {
                val projection =
                    arrayOf(
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
                        } else {
                            BUCKET_DISPLAY_NAME
                        }
                    )

                val selection =
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                val selectionArgs = arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                )

                val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

                application.contentResolver.query(
                    MediaStore.Files.getContentUri(VOLUME_EXTERNAL),
                    projection,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                    val bucketColumn =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                        } else {
                            cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)
                        }

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)
                        val bucket = cursor.getString(bucketColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val storageFile = StorageFile(
                            id = id,
                            displayName = displayName,
                            mimeType = mimeType,
                            bucket = bucket,
                            contentUri = contentUri
                        )

                        storages.add(storageFile)
                    }
                }


            }

            return storages
        }
    }
}