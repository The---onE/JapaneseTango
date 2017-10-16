package com.xmx.tango.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.annotation.StringRes
import android.util.Log
import android.widget.Toast

import java.util.ArrayList

/**
 * 字符串工具类
 *
 * @author xiaoleilu
 */
object StrUtil {

    /**
     * 显示提示信息
     * @param context 当前上下文
     * @param str 要显示的字符串信息
     */
    fun showToast(context: Context, str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示提示信息
     * @param context 当前上下文
     * @param resId 要显示的字符串在strings文件中的ID
     */
    fun showToast(context: Context, @StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param msg 日志信息
     */
    fun showLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param i 数字作为日志信息
     */
    fun showLog(tag: String, i: Int) {
        Log.e(tag, "" + i)
    }

    /**
     * 复制到剪贴板
     * @param context 当前上下文
     * @param text 要复制的内容
     */
    fun copyToClipboard(context: Context, text: String) {
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text) //文本型数据 clipData 的构造方法。
        cmb.primaryClip = clipData
    }
}

