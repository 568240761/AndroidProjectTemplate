package com.ly.module.image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.FutureTarget
import com.ly.module.log.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by Lan Yang on 2020/9/12
 *
 * 图片加载管理
 */
object ImageLoader {

    private val tag = this.javaClass.simpleName

    /**不设置占位图*/
    const val IMAGE_NO_PLACEHOLDER = -1

    /**默认占位图*/
    @DrawableRes
    var IMAGE_PLACEHOLDER = R.drawable.image_shape_placeholder

    /**默认拉取失败占位图*/
    @DrawableRes
    var IMAGE_PLACEHOLDER_ERROR = R.drawable.image_shape_placeholder

    /**
     * 在[imageView]上展示图片；
     *
     * Glide会根据[FragmentActivity]或[Fragment]的生命周期，自动展示图片、取消加载图片、回收图片资源。
     *
     * @param context     可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param imageView   展示图片的 [ImageView]
     * @param any         可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param placeholder 占位符资源id；为[IMAGE_NO_PLACEHOLDER]时，设置占位符
     * @param error       错误符资源id；为[IMAGE_NO_PLACEHOLDER]时，不设置错误占位符
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    fun showImage(
        context: Any,
        imageView: ImageView,
        any: Any,
        placeholder: Int = IMAGE_PLACEHOLDER,
        error: Int = IMAGE_PLACEHOLDER_ERROR,
        diskCache: Boolean = true,
        memoryCache: Boolean = true
    ) {
        val glideRequests = createGlideRequests(context)

        var glideRequest = createGlideRequest(glideRequests, any)

        if (!diskCache) {
            glideRequest = glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE)
        }

        if (!memoryCache) {
            glideRequest = glideRequest.skipMemoryCache(true)
        }

        if (placeholder != IMAGE_NO_PLACEHOLDER)
            glideRequest = glideRequest.placeholder(placeholder)

        if (error != IMAGE_NO_PLACEHOLDER)
            glideRequest = glideRequest.error(error)

        //调用 into() 方法会检查 imageView 上是否存在没有执行完成的加载图片操作；
        //如果存在，该加载图片操作会被取消。
        glideRequest.into(imageView)
    }

    /**
     * 将下载的资源转换为[Bitmap]
     *
     * @param context 可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param any     可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    suspend fun downloadBitmap(
        context: Any,
        any: Any,
        diskCache: Boolean = true,
        memoryCache: Boolean = true
    ): Bitmap? {
        val glideRequests = createGlideRequests(context)
        val glideRequest = glideRequests.asBitmap()
        return getResource(
            any = any,
            request = glideRequest,
            diskCache = diskCache,
            memoryCache = memoryCache
        )
    }

    /**
     * 将下载的资源转换为[Drawable]
     *
     * @param context 可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param any     可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    suspend fun downloadDrawable(
        context: Any,
        any: Any,
        diskCache: Boolean = true,
        memoryCache: Boolean = true
    ): Drawable? {
        val glideRequests = createGlideRequests(context)
        val glideRequest = glideRequests.asDrawable()
        return getResource(
            any = any,
            request = glideRequest,
            diskCache = diskCache,
            memoryCache = memoryCache
        )
    }

    /**
     * 将下载的资源转换为[GifDrawable]
     *
     * @param context 可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param any     可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    suspend fun downloadGif(
        context: Any,
        any: Any,
        diskCache: Boolean = true,
        memoryCache: Boolean = true
    ): GifDrawable? {
        val glideRequests = createGlideRequests(context)
        val glideRequest = glideRequests.asGif()
        return getResource(
            any = any,
            request = glideRequest,
            diskCache = diskCache,
            memoryCache = memoryCache
        )
    }

    /**
     * 将下载的资源转换为[File]
     *
     * @param context 可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param any     可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    suspend fun downloadFile(
        context: Any,
        any: Any,
        diskCache: Boolean = true,
        memoryCache: Boolean = true
    ): File? {
        val glideRequests = createGlideRequests(context)
        val glideRequest = glideRequests.asFile()
        return getResource(
            any = any,
            request = glideRequest,
            diskCache = diskCache,
            memoryCache = memoryCache
        )
    }

    /**
     * 下载资源
     *
     * @param any   可传入：[Uri]、[Drawable]、[Bitmap]、[String]、文件、字节数组、图片相关的资源文件
     * @param diskCache   默认将图片缓存到磁盘中，false 表示不缓存到磁盘中；
     * @param memoryCache 默认将图片缓存到内存中，false 表示不缓存到内存中；如果下载的资源占用内存非常大，可以考虑不缓存到内存中；
     *
     * 注意，当[diskCache]为true时，图片有可能不会放入磁盘缓存中；当[memoryCache]为true时，图片有可能不会放入内存缓存中；
     * 比如：
     * 当[any]为[Drawable]或[Bitmap]时，不会将图片缓存到磁盘中；
     * 当[any]为字节数组或图片资源文件时，也会有不同的缓存处理；
     * 具体见[com.bumptech.glide.RequestBuilder]中的代码。
     */
    private suspend fun <T> getResource(
        any: Any,
        request: GlideRequest<T>,
        diskCache: Boolean,
        memoryCache: Boolean
    ): T? {
        var glideRequest = createGlideRequest(request, any)

        if (!diskCache) {
            glideRequest = glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE)
        }

        if (!memoryCache) {
            glideRequest = glideRequest.skipMemoryCache(true)
        }

        return withContext(Dispatchers.IO) {
            var futureTarget: FutureTarget<T>? = null
            var result: T? = null
            try {
                futureTarget = glideRequest.submit()
                result = futureTarget.get()
            } catch (e: Exception) {
                logError("$tag-getResource", "", e)
            } finally {
                futureTarget?.cancel(true)
            }
            return@withContext result
        }
    }

    /**
     * 取消正在加载的图片
     *
     * @param context   可传入：[Context]、[FragmentActivity]、[Fragment]、[Activity]、[View]
     * @param imageView 展示图片的 [ImageView]
     */
    fun cancel(
        context: Any,
        imageView: ImageView
    ) {
        val glideRequests = createGlideRequests(context)
        glideRequests.clear(imageView)
    }

    /**清除尽可能多的内存缓存*/
    fun clearMemory(context: Context) = GlideApp.get(context).clearMemory()

    /**清除磁盘上缓存*/
    suspend fun clearDiskCache(context: Context) {
        withContext(Dispatchers.IO) {
            GlideApp.get(context).clearDiskCache()
        }
    }

    private fun createGlideRequests(context: Any) = when (context) {
        is FragmentActivity -> GlideApp.with(context)
        is Activity -> GlideApp.with(context)
        is Fragment -> GlideApp.with(context)
        is Context -> GlideApp.with(context)
        is View -> GlideApp.with(context)
        else -> throw IllegalArgumentException("传入的参数[context]不合理")
    }

    private fun createGlideRequest(
        glideRequests: GlideRequests,
        any: Any
    ) = when (any) {
        is String -> glideRequests.load(any)
        is Bitmap -> glideRequests.load(any)
        is Drawable -> glideRequests.load(any)
        is File -> glideRequests.load(any)
        is Uri -> glideRequests.load(any)
        is Int -> glideRequests.load(any)
        is ByteArray -> glideRequests.load(any)
        else -> throw IllegalArgumentException("传入的参数[any]不合理")
    }

    private fun <T> createGlideRequest(
        glideRequest: GlideRequest<T>,
        any: Any
    ) = when (any) {
        is String -> glideRequest.load(any)
        is Bitmap -> glideRequest.load(any)
        is Drawable -> glideRequest.load(any)
        is File -> glideRequest.load(any)
        is Uri -> glideRequest.load(any)
        is Int -> glideRequest.load(any)
        is ByteArray -> glideRequest.load(any)
        else -> throw IllegalArgumentException("传入的参数[any]不合理")
    }
}