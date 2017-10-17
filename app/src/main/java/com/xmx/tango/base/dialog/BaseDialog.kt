package com.xmx.tango.base.dialog

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xmx.tango.utils.StrUtil

/**
 * Created by The_onE on 2017/5/19.
 * Dialog基类，声明业务接口，提供常用功能
 */
abstract class BaseDialog : DialogFragment() {
    var mContext: Context? = null

    /**
     * 初始化Dialog，可在子类中重载
     * @param context 当前上下文
     */
    fun initDialog(context: Context) {
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            getContentView(inflater, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mContext == null) {
            showToast("请先初始化Dialog")
            return
        }
        // 初始化View
        initView(view, savedInstanceState)
        // 声明事件监听
        setListener(view)
        // 处理业务逻辑
        processLogic(view, savedInstanceState)
    }

    /**
     * 设置Fragment对应布局接口
     * 实现中return inflater.inflate(R.layout.Dialog布局, container);
     * @param inflater 用于加载Fragment的布局
     * @param container 存放Fragment的layout的ViewGroup
     * @return Fragment对应的布局
     */
    protected abstract fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View

    /**
     * 初始化View接口
     * @param view Fragment对应布局的View
     * @param savedInstanceState [onViewCreated]方法中的实例状态
     */
    protected abstract fun initView(view: View, savedInstanceState: Bundle?)

    /**
     * 声明事件监听接口
     * @param view Fragment对应布局的View
     */
    protected abstract fun setListener(view: View)

    /**
     * 处理业务逻辑接口
     * @param view Fragment对应布局的View
     * @param savedInstanceState [onViewCreated]方法中的实例状态
     */
    protected abstract fun processLogic(view: View, savedInstanceState: Bundle?)

    /**
     * 显示提示信息
     * @param str 要显示的字符串信息
     */
    protected fun showToast(str: String) {
        StrUtil.showToast(mContext, str)
    }

    /**
     * 显示提示信息
     * @param resId 要显示的字符串在strings文件中的ID
     */
    protected fun showToast(@StringRes resId: Int) {
        StrUtil.showToast(mContext, resId)
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param msg 日志信息
     */
    protected fun showLog(tag: String, msg: String) {
        StrUtil.showLog(tag, msg)
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param i 数字作为日志信息
     */
    protected fun showLog(tag: String, i: Int) {
        StrUtil.showLog(tag, i)
    }
}
