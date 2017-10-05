package com.xmx.tango.utils

import android.content.Context
import android.os.Vibrator

/**
 * Created by The_onE on 2017/1/31.
 * 震动控制器
 */

object VibratorUtil {
    /**
     * 获取系统震动器
     * @param[context] 当前上下文
     * @return 系统震动器
     */
    private fun getVibrator(context: Context): Vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    /**
     * 震动一定时间
     * @param[context] 当前上下文
     * @param[time] 震动时间
     */
    fun vibrate(context: Context, time: Long) {
        val vibrator = getVibrator(context)
        vibrator.vibrate(time)
    }

    /**
     * 持续震动
     * @param[context] 当前上下文
     */
    fun vibrate(context: Context) {
        val vibrator = getVibrator(context)
        vibrator.vibrate(longArrayOf(0, 10000), 0)
    }

    /**
     * 交替震动，先震动，后暂停，不断反复
     * @param[context] 当前上下文
     * @param[vibrateTime] 震动时间
     * @param[pauseTime] 暂停时间
     * @param[repeatTimes] 重复次数，0则不断重复
     */
    fun vibrate(context: Context, vibrateTime: Long, pauseTime: Long, repeatTimes: Int) {
        val vibrator = getVibrator(context)
        if (repeatTimes <= 0) {
            // 不断重复
            vibrator.vibrate(longArrayOf(pauseTime, vibrateTime), 0)
        } else {
            // 0, V, P, V, P, V, ...
            val array = Array<Long>(repeatTimes * 2, { i ->
                when (i) {
                    0 -> 0
                    else -> when (i % 2) {
                        1 -> vibrateTime
                        0 -> pauseTime
                        else -> 0
                    }
                }
            })
            vibrator.vibrate(array.toLongArray(), -1)
        }
    }

    /**
     * 官方震动
     * @param[context] 当前上下文
     * @param[pattern] 震动序列(P, V, P, V, ...)
     * @param[repeat] 从指定的下标开始重复震动，-1则不重复
     */
    fun vibrate(context: Context, pattern: LongArray, repeat: Int) {
        val vibrator = getVibrator(context)
        vibrator.vibrate(pattern, repeat)
    }

    /**
     * 取消震动
     * @param[context] 当前上下文
     */
    fun cancel(context: Context) {
        val vibrator = getVibrator(context)
        vibrator.cancel()
    }
}
