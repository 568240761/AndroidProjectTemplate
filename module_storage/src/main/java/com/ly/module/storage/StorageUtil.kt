package com.ly.module.storage

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by Lan Yang on 2020/9/18
 *
 * 媒体文件访问的工具类
 *
 * 关于从共享存储空间访问文档和其他文件，可以参考官方文档[https://developer.android.google.cn/training/data-storage/shared/documents-files?hl=zh-CN#use-cases]
 */
class StorageUtil {
    companion object {

        /**Android 10+ 开始，该字段从[MediaStore.Images.ImageColumns]、[MediaStore.Video.VideoColumns]移入[MediaStore.MediaColumns]中*/
        private const val BUCKET_DISPLAY_NAME = "bucket_display_name"

        /**
         * 查询外部存储空间上的图片；
         *
         * 从Android 10开始，查询非本应用创建的图片，需要读权限；
         * 本应用创建的图片，不需要任何权限。
         */
        suspend fun queryImages(application: Application): List<ImageFile> {
            val images = mutableListOf<ImageFile>()

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withContext(Dispatchers.IO) {

                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

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
                        uri,
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

                            val contentUri = ContentUris.withAppendedId(uri, id)

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
            } else {
                throw IllegalStateException("未授予读权限")
            }
            return images
        }

        /**
         * 查询外部存储空间上的视频
         *
         * 从Android 10开始，查询非本应用创建的视频，需要读权限；
         * 本应用创建的视频，不需要任何权限。
         */
        suspend fun queryVideos(application: Application): List<VideoFile> {
            val videos = mutableListOf<VideoFile>()

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withContext(Dispatchers.IO) {

                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Video.Media.getContentUri(VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

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
                        uri,
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

                            val contentUri = ContentUris.withAppendedId(uri, id)

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
            } else {
                throw IllegalStateException("未授予读权限")
            }
            return videos
        }

        /**
         * 查询外部存储空间上的音频
         *
         * 从Android 10开始，查询非本应用创建的音频，需要读权限；
         * 本应用创建的音频，不需要任何权限。
         */
        suspend fun queryAudios(application: Application): List<AudioFile> {
            val audios = mutableListOf<AudioFile>()

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withContext(Dispatchers.IO) {

                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val projection =
                        arrayOf(
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.MIME_TYPE
                        )

                    val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

                    application.contentResolver.query(
                        uri,
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

                            val contentUri = ContentUris.withAppendedId(uri, id)

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
            } else {
                throw IllegalStateException("未授予读权限")
            }
            return audios
        }

        /**
         * 查询外部存储空间上的图片和视频
         *
         * 从Android 10开始，查询非本应用创建的媒体文件，需要读权限；
         * 本应用创建的媒体文件，不需要任何权限。
         */
        suspend fun queryImagesAndVideos(application: Application): List<StorageFile> {
            val medias = mutableListOf<StorageFile>()

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                withContext(Dispatchers.IO) {
                    val uri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.Files.getContentUri(VOLUME_EXTERNAL)
                        } else {
                            MediaStore.Files.getContentUri("external")
                        }

                    val projection =
                        arrayOf(
                            MediaStore.Files.FileColumns._ID,
                            MediaStore.Files.FileColumns.DISPLAY_NAME,
                            MediaStore.Files.FileColumns.MIME_TYPE
                        )

                    val selection =
                        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

                    val selectionArgs = arrayOf(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
                    )

                    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

                    application.contentResolver.query(
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                    )?.use { cursor ->
                        val idColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                        val displayNameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                        val mimeTypeColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idColumn)
                            val displayName = cursor.getString(displayNameColumn)
                            val mimeType = cursor.getString(mimeTypeColumn)
                            val contentUri = ContentUris.withAppendedId(uri, id)

                            val mediaFile = StorageFile(
                                id = id,
                                displayName = displayName,
                                mimeType = mimeType,
                                contentUri = contentUri
                            )

                            medias.add(mediaFile)
                        }
                    }
                }
            } else {
                throw IllegalStateException("未授予读权限")
            }

            return medias
        }

        /**
         * 与其他应用共享图片，图片存放在 DCIM/[parentFile]/ 或 DCIM/ 下
         *
         * 从Android 10开始，创建的媒体文件，不需要写权限；
         */
        suspend fun saveImage(
            application: Application,
            inputStream: InputStream,
            displayName: String,
            parentFile: String? = null,
            completed: (() -> Unit)? = null
        ) {
            if (displayName.isEmpty()) throw IllegalArgumentException("图片名称不能为空")

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                throw IllegalStateException("未授予写权限")
            }

            withContext(Dispatchers.IO) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName)

                val mimeType = when {
                    displayName.endsWith(".jpg") -> "image/jpeg"
                    displayName.endsWith(".jpeg") -> "image/jpeg"
                    displayName.endsWith(".png") -> "image/png"
                    displayName.endsWith(".webp") -> "image/webp"
                    else -> "image/*"
                }
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        if (parentFile.isNullOrEmpty()) Environment.DIRECTORY_DCIM else "${Environment.DIRECTORY_DCIM}/$parentFile"
                    )
                } else {
                    val path =
                        "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}"

                    if (parentFile != null) {
                        val file = File("$path/$parentFile/")
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                    }

                    values.put(
                        MediaStore.Images.Media.DATA,
                        if (parentFile.isNullOrEmpty()) "$path/$displayName" else "$path/$parentFile/$displayName"
                    )
                }

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(VOLUME_EXTERNAL)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                application.contentResolver.insert(uri, values)?.also {
                    application.contentResolver.openOutputStream(it)
                        ?.also { outputStream: OutputStream ->
                            inputStream.copyTo(outputStream)
                            outputStream.close()
                            inputStream.close()

                            withContext(Dispatchers.Main) {
                                completed?.invoke()
                            }
                        }
                }
            }
        }

        /**
         * 与其他应用共享视频，视频存放在 DCIM/[parentFile]/ 或 DCIM 下
         *
         * 从Android 10开始，创建的媒体文件，不需要写权限；
         */
        suspend fun saveVideo(
            application: Application,
            inputStream: InputStream,
            displayName: String,
            parentFile: String? = null,
            completed: (() -> Unit)? = null
        ) {
            if (displayName.isEmpty()) throw IllegalArgumentException("图片名称不能为空")

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                throw IllegalStateException("未授予写权限")
            }

            withContext(Dispatchers.IO) {
                val values = ContentValues()
                values.put(MediaStore.Video.Media.DISPLAY_NAME, displayName)

                val mimeType = when {
                    displayName.endsWith(".mp4") -> "video/mp4"
                    displayName.endsWith(".3gp") -> "video/3gpp"
                    displayName.endsWith(".webm") -> "video/webm"
                    displayName.endsWith(".mkv") -> "video/x-matroska"
                    else -> "video/*"
                }
                values.put(MediaStore.Video.Media.MIME_TYPE, mimeType)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(
                        MediaStore.Video.Media.RELATIVE_PATH,
                        if (parentFile.isNullOrEmpty()) Environment.DIRECTORY_DCIM else "${Environment.DIRECTORY_DCIM}/$parentFile"
                    )
                } else {
                    val path =
                        "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}"

                    if (parentFile != null) {
                        val file = File("$path/$parentFile/")
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                    }

                    values.put(
                        MediaStore.Video.Media.DATA,
                        if (parentFile.isNullOrEmpty()) "$path/$displayName" else "$path/$parentFile/$displayName"
                    )
                }

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

                application.contentResolver.insert(uri, values)?.also {
                    application.contentResolver.openOutputStream(it)
                        ?.also { outputStream: OutputStream ->
                            inputStream.copyTo(outputStream)
                            outputStream.close()
                            inputStream.close()

                            withContext(Dispatchers.Main) {
                                completed?.invoke()
                            }
                        }
                }
            }
        }

        /**
         * 存放文件到非应用专属目录，考虑到 Android 10 开始的分区存储，文件存放在 Download 下
         *
         * 从Android 10开始，创建文件，不需要写权限；
         */
        suspend fun saveFile(
            application: Application,
            inputStream: InputStream,
            displayName: String,
            parentFile: String? = null,
            completed: (() -> Unit)? = null
        ) {
            if (displayName.isEmpty()) throw IllegalArgumentException("文件名称不能为空")

            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                throw IllegalStateException("未授予写权限")
            }

            withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues()
                    values.put(MediaStore.Downloads.DISPLAY_NAME, displayName)
                    values.put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        if (parentFile == null) Environment.DIRECTORY_DOWNLOADS else "${Environment.DIRECTORY_DOWNLOADS}/$parentFile"
                    )

                    application.contentResolver.insert(
                        MediaStore.Downloads.getContentUri(
                            VOLUME_EXTERNAL
                        ), values
                    )?.also {
                        application.contentResolver.openOutputStream(it)
                            ?.also { outputStream: OutputStream ->
                                inputStream.copyTo(outputStream)
                                outputStream.close()
                                inputStream.close()

                                withContext(Dispatchers.Main) {
                                    completed?.invoke()
                                }
                            }
                    }
                } else {
                    val rootPath =
                        "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DOWNLOADS}"

                    val path = if (parentFile.isNullOrEmpty())
                        "$rootPath/$displayName"
                    else
                        "$rootPath/$parentFile/$displayName"

                    val file = File(path)
                    val parentDirectory = file.parentFile

                    parentDirectory?.also {
                        if (!it.exists())
                            it.mkdirs()
                    }

                    if (!file.exists()) {
                        file.createNewFile()
                    }

                    val fileOutputStream = FileOutputStream(file)
                    inputStream.copyTo(fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()

                    withContext(Dispatchers.Main) {
                        completed?.invoke()
                    }
                }
            }
        }

        /**
         * 打开媒体文件
         */
        suspend fun openFile(application: Application, uri: Uri): InputStream? {
            return withContext(Dispatchers.IO) {
                if (
                    ContextCompat.checkSelfPermission(
                        application,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    application.contentResolver.openInputStream(uri)
                } else {
                    throw IllegalStateException("未授予读权限")
                }
            }
        }

        /**
         * 删除媒体文件；
         *
         * 从Android 10开始，对于非本应用创建的媒体文件，需要用户授权。
         * 用户同意后，onActivityResult中再次调用该方法才可以删除
         */
        suspend fun deleteFile(
            activity: Activity,
            uri: Uri,
            id: Long,
            completed: (() -> Unit)? = null
        ) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                throw IllegalStateException("未授予写权限")
            }

            withContext(Dispatchers.IO) {
                try {
                    val row = activity.contentResolver.delete(
                        uri,
                        "${MediaStore.MediaColumns._ID} = ?",
                        arrayOf(id.toString())
                    )

                    completed?.also {
                        if (row >= 0)
                            withContext(Dispatchers.Main) {
                                it.invoke()
                            }
                    }
                } catch (securityException: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val recoverableSecurityException =
                            securityException as? RecoverableSecurityException
                                ?: throw securityException

                        val intentSender =
                            recoverableSecurityException.userAction.actionIntent.intentSender
                        intentSender?.let {
                            startIntentSenderForResult(
                                activity,
                                intentSender,
                                STORAGE_DELETE_FILE_PERMISSION_REQUEST,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        }
                    } else {
                        throw securityException
                    }
                }
            }
        }


        /**
         * 更新媒体文件；
         *
         * 从Android 10开始，对于非本应用创建的媒体文件，需要用户授权。
         * 用户同意后，onActivityResult中再次调用该方法才可以更新。
         *
         * [inputStream]用于更改文件的内容；[update]用于更改文件信息，比如文件名称。
         */
        suspend fun updateFile(
            activity: Activity,
            uri: Uri,
            id: Long,
            inputStream: InputStream? = null,
            update: ContentValues? = null,
            completed: (() -> Unit)? = null
        ) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                throw IllegalStateException("未授予写权限")
            }

            withContext(Dispatchers.IO) {
                try {
                    if (inputStream != null && update != null)
                        throw IllegalArgumentException("参数[inputStream]与[update]不能同时为值！")

                    when {
                        inputStream != null -> {
                            activity.contentResolver.openOutputStream(uri)?.use {
                                inputStream.copyTo(it)
                                it.close()
                                inputStream.close()

                                completed?.also {
                                    withContext(Dispatchers.Main) {
                                        it.invoke()
                                    }
                                }
                            }
                        }

                        update != null -> {
                            val row = activity.contentResolver.update(
                                uri,
                                update,
                                "${MediaStore.MediaColumns._ID} = ?",
                                arrayOf(id.toString())
                            )

                            completed?.also {
                                if (row >= 0)
                                    withContext(Dispatchers.Main) {
                                        it.invoke()
                                    }
                            }
                        }

                        else -> {
                            throw IllegalArgumentException("参数[inputStream]与[update]不能同时为空！")
                        }
                    }
                } catch (securityException: SecurityException) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val recoverableSecurityException =
                            securityException as? RecoverableSecurityException
                                ?: throw securityException

                        val intentSender =
                            recoverableSecurityException.userAction.actionIntent.intentSender
                        intentSender?.let {
                            startIntentSenderForResult(
                                activity,
                                intentSender,
                                STORAGE_UPDATE_FILE_PERMISSION_REQUEST,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        }
                    } else {
                        throw securityException
                    }
                }
            }
        }
    }
}