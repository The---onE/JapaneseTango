package com.xmx.tango.core

import java.util.Date

/**
 * Created by The_onE on 2016/2/24.
 * 用于应用核心功能的常量
 */
object CoreConstants {
    val EXCEPTION_DEBUG = true // 是否显示异常信息
    val DEBUG_MODE = true // 是否为调试模式

    val APP_ID = "JU6HFVAzc5KrBnJBTMc7QbTe-gzGzoHsz" // LeanCloud应用ID
    val APP_KEY = "ptscFhYmavGYVvIGCikAczN2" // LeanCloud应用KEY

    val FILE_DIR = "/JapaneseTango" // 应用数据目录
    val DATABASE_DIR = FILE_DIR + "/Database" // 数据库目录
    val DATABASE_FILE = DATABASE_DIR + "/database.db" // 数据库文件

    val LONGEST_EXIT_TIME: Long = 2000 // 再按一次退出程序响应时间
    val SPLASH_TIME: Long = 2000 // 启动页自动跳转时间
    val LONGEST_SPLASH_TIME: Long = 6000 // 启动页等待登录成功最长事件

    val SECOND_TIME: Long = 1000 // 一秒钟毫秒数
    val MINUTE_TIME = 60 * SECOND_TIME // 一分钟毫秒数
    val HOUR_TIME = 60 * MINUTE_TIME // 一小时毫秒数
    val DAY_TIME = 24 * HOUR_TIME // 一天毫米数

    // 各个月份的天数
    val DAYS_OF_MONTH = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    // 一周各天的名称，从周日开始
    val DAY_OF_WEEK = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

    fun isSameDate(now: Date, last: Date): Boolean =
            now.time - last.time < CoreConstants.DAY_TIME && now.date == last.date
}
