package com.xmx.tango.utils

import android.content.Context

/**
 * Created by The_onE on 2017/2/20.
 * 屏幕长度单位转换工具类
 */
object DisplayUtil {
    /**
     * 将px值转换为dp值
     * @param[context] 当前上下文
     * @param[pxValue] 原px值
     * @return 转化为dp值
     */
    fun px2dp(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f)
    }

    /**
     * 将dp值转换为px值
     * @param[context] 当前上下文
     * @param[dpValue] 原dp值
     * @return 转化为px值
     */
    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 将px值转换为sp值
     * @param[context] 当前上下文
     * @param[pxValue] 原px值
     * @return 转化为sp值
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值
     * @param[context] 当前上下文
     * @param[spValue] 原sp值
     * @return 转化为px值
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}