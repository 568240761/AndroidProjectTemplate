package com.ly.module.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Lan Yang on 2020/8/30
 *
 * 时间相关的工具类
 */
class DateUtil {
    companion object {
        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_FORMAT_ONE = "yyyy.MM.dd"
        const val DATE_FORMAT_TWO = "yyyy-MM-dd HH:mm:ss"

        /**
         * 转换日期格式
         *
         * @param dateString 原日期
         * @param format 日期格式
         * @return 字符串格式日期
         */
        fun convertDateFormat(dateString: String, format: String): String {
            val dateFormat = SimpleDateFormat(format, Locale.CHINA)
            try {
                return dateFormat.format(dateFormat.parse(dateString)!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateString
        }

        /**
         * 将Date转化为字符串
         *
         * @param date date为null时，返回当前日期字符串
         * @param format 日期格式；默认为 yyyy-MM-dd
         * @return 字符串格式日期
         */
        fun parseDateToString(date: Date? = null, format: String = DEFAULT_DATE_FORMAT): String {
            var time = date
            if (time == null) {
                time = Date()
            }

            val sdf = SimpleDateFormat(format, Locale.CHINA)
            return sdf.format(time)
        }

        /**
         * 将字符串转化为Date
         *
         * @param dateString 日期文本
         * @param format 日期格式；默认为 yyyy-MM-dd
         * @return 成功返回其日期，失败返回当前日期
         */
        fun parseStringToDate(
            dateString: String,
            format: String = DEFAULT_DATE_FORMAT
        ): Date {
            val dateFormat = SimpleDateFormat(format, Locale.CHINA)
            try {
                return dateFormat.parse(dateString)!!
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return Date()
        }

        /**
         * 将字符串转化为Calender
         *
         * @param dateString 日期文本
         * @param format 日期格式；默认为 yyyy-MM-dd
         * @return 成功返回其日期，失败返回当前日期
         */
        fun parseStringToCalender(
            dateString: String,
            format: String = DEFAULT_DATE_FORMAT
        ): Calendar {
            val dateFormat = SimpleDateFormat(format, Locale.CHINA)
            val calendar = Calendar.getInstance()
            try {
                val date = dateFormat.parse(dateString)!!
                calendar.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return calendar
        }


        /**
         * 以[time]为基准，得到往后或前[step]个月的第一天
         *
         * @param step 增减月份；为负数,表示往前;为正数,表示往后
         * @param format 默认为 yyyy-MM-dd
         * @param time 基准时间
         * @return 字符串格式日期
         */
        fun getFirstDayInMonth(
            step: Int,
            format: String = DEFAULT_DATE_FORMAT,
            time: Calendar? = null
        ): String {
            var calendar = Calendar.getInstance()
            if (time != null) calendar = time
            calendar.add(Calendar.MONTH, step)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            val date = calendar.time
            return parseDateToString(date, format)
        }

        /**
         * 以[time]为基准，得到往后或前[step]个月的最后一天
         *
         * @param step 增减月份；为负数,表示往前;为正数,表示往后
         * @param format 默认为 yyyy-MM-dd
         * @param time 基准时间
         * @return 字符串格式日期
         */
        fun getLastDayInMonth(
            step: Int,
            format: String = DEFAULT_DATE_FORMAT,
            time: Calendar? = null
        ): String {
            var calendar = Calendar.getInstance()
            if (time != null) calendar = time
            calendar.add(Calendar.MONTH, step)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val date = calendar.time
            return parseDateToString(date, format)
        }

        /**
         * 以[time]为基准，往后或前[step]个天
         *
         * @param step 增减天数；为负数,表示往前;为正数,表示往后
         * @param format 默认为 yyyy-MM-dd
         * @param time 基准时间
         * @return 字符串格式日期
         */
        fun getOneDay(
            step: Int,
            format: String = DEFAULT_DATE_FORMAT,
            time: Calendar? = null
        ): String {
            var calendar = Calendar.getInstance()
            if (time != null) calendar = time
            calendar.add(Calendar.DAY_OF_MONTH, step)
            val date = calendar.time
            return parseDateToString(date, format)
        }

        /**
         * 以[time]为基准，往后或前[step]个周
         *
         * @param step 增减周数；为负数,表示往前;为正数,表示往后
         * @param format 默认为 yyyy-MM-dd
         * @param time 基准时间
         * @return 字符串格式日期
         */
        fun getWeek(
            step: Int,
            format: String = DEFAULT_DATE_FORMAT,
            time: Calendar? = null
        ): String {
            var calendar = Calendar.getInstance()
            if (time != null) calendar = time
            calendar.add(Calendar.WEEK_OF_YEAR, step)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            val date = calendar.time
            return parseDateToString(date, format)
        }

        /**
         * 以[time]为基准，往后或前[step]个月
         *
         * @param step 增减月份；为负数,表示往前;为正数,表示往后
         * @param format 默认为 yyyy-MM-dd
         * @param time 基准时间
         * @return 字符串格式日期
         */
        fun getMonth(
            step: Int,
            format: String = DEFAULT_DATE_FORMAT,
            time: Calendar? = null
        ): String {
            var calendar = Calendar.getInstance()
            if (time != null) calendar = time
            calendar.add(Calendar.MONTH, step)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val date = calendar.time
            return parseDateToString(date, format)
        }

        /**
         * 得到两个日期之间的天数
         *
         * @param firstDateString 第一个日期
         * @param secondDateString 第二个日期
         * @param format 默认为 yyyy-MM-dd
         * @return 相差天数；失败，返回-1
         */
        fun getBetweenDay(
            firstDateString: String,
            secondDateString: String,
            format: String = DEFAULT_DATE_FORMAT
        ): Long {
            try {
                val sdf = SimpleDateFormat(format, Locale.CHINA)

                val firstDate = sdf.parse(firstDateString)!!
                val secondDate = sdf.parse(secondDateString)!!

                val betweenTime = if (secondDate.time > firstDate.time) {
                    secondDate.time - firstDate.time
                } else {
                    firstDate.time - secondDate.time
                }

                return betweenTime / (24 * 60 * 60 * 1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return -1L
        }
    }
}