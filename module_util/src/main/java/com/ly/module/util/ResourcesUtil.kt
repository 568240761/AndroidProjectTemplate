package com.ly.module.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.StringRes

/**
 * Created by Lan Yang on 2020/8/20
 * 关于[Resources]相关的工具类
 */
@Suppress("KDocUnresolvedReference")
class ResourcesUtil {
    companion object {

        /**
         * 返回字符串资源[id]对应的字符串
         */
        fun getString(context: Context, @StringRes id: Int): String {
            return context.resources.getString(id)
        }

        /**
         * 返回屏幕宽度，单位px
         */
        fun getScreenWidthPx(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        /**
         * 返回屏幕高度，单位px
         */
        fun getScreenHeightPx(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }


        private fun getDensity(context: Context): Float {
            return context.resources.displayMetrics.density
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale =
                getDensity(context)
            return (dpValue * scale).toInt()
        }

        /**
         * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
         */
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale =
                getDensity(context)
            return (pxValue / scale).toInt()
        }

        /**
         * 返回屏幕宽度，单位dp
         */
        fun retScreenWidthDp(context: Context): Int {
            return px2dip(
                context,
                getScreenWidthPx(
                    context
                ).toFloat()
            )
        }

        /**
         * 返回屏幕高度，单位dp
         */
        fun retScreenHeightDp(context: Context): Int {
            return px2dip(
                context,
                getScreenHeightPx(
                    context
                ).toFloat()
            )
        }

        /**
         * 获取状态栏高度
         */
        fun getStatusHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 获取ActionBar高度
         */
        fun getActionBarHeight(context: Context): Int {
            var actionBarHeight = 0
            val value = TypedValue()
            if (context.theme.resolveAttribute(android.R.attr.actionBarSize, value, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    value.data,
                    context.resources.displayMetrics
                )
            }
            return actionBarHeight
        }

        /**
         * 获取底部导航栏高度
         */
        fun getNavigationBarHeight(context: Context): Int {
            var result = 0
            val show =
                context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
            if (show != 0) {
                val resourceId =
                    context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    result = context.resources.getDimensionPixelSize(resourceId)
                }
            }
            return result
        }
    }
}