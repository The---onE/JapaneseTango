package com.xmx.tango.base.fragment

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xmx.tango.utils.StrUtil

/**
 * Created by The_onE on 2016/7/4.
 * Fragment基类，声明业务接口，提供常用功能
 */
abstract class BaseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            // 设置Fragment对应布局
            getContentView(inflater, container)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.apply {
            // 初始化View
            initView(this, savedInstanceState)
            // 声明事件监听
            setListener(this)
            // 处理业务逻辑
            processLogic(this, savedInstanceState)
        }
    }

    /**
     * 设置Fragment对应布局接口
     * 实现中return inflater.inflate(R.layout.Fragment布局, container, false);
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
        StrUtil.showToast(context, str)
    }

    /**
     * 显示提示信息
     * @param resId 要显示的字符串在strings文件中的ID
     */
    protected fun showToast(@StringRes resId: Int) {
        StrUtil.showToast(context, resId)
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

    /**
     * 启动新Activity
     * @param cls 要启动的Activity类，格式为 Activity名::class.java
     */
    protected fun startActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        startActivity(intent)
    }

    /**
     * 带参数启动新Activity
     * @param cls 要启动的Activity类，格式为 Activity名::class.java
     * @param objects 向Activity传递的参数，奇数项为键，偶数项为值
     */
    protected fun startActivity(cls: Class<*>, vararg objects: String) {
        val intent = Intent(context, cls)
        var i = 0
        while (i < objects.size) {
            intent.putExtra(objects[i], objects[++i])
            i++
        }
        startActivity(intent)
    }
}
