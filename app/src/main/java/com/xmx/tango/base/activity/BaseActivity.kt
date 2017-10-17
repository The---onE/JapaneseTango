package com.xmx.tango.base.activity

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

import com.xmx.tango.core.MyApplication
import com.xmx.tango.utils.StrUtil

/**
 * Created by The_onE on 2015/12/27.
 * Activity基类，声明业务接口，提供常用功能
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在Application中集中管理
        MyApplication.getInstance().addActivity(this)

        // 初始化View
        initView(savedInstanceState)
        // 声明事件监听
        setListener()
        // 处理业务逻辑
        processLogic(savedInstanceState)
    }

    // 在Kotlin中无需获取控件对象，直接使用ID即可
    // import kotlinx.android.synthetic.布局文件.*
    // 控件ID.setText(文本)
    // 控件ID.setOnClickListener { 点击处理 }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getInstance().removeActivity(this)
    }

    /**
     * 初始化View接口
     * @param savedInstanceState  [onCreate]方法中的实例状态
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 声明事件监听接口
     */
    protected abstract fun setListener()

    /**
     * 处理业务逻辑接口
     * @param savedInstanceState [onCreate]方法中的实例状态
     */
    protected abstract fun processLogic(savedInstanceState: Bundle?)

    /**
     * 显示提示信息
     * @param str 要显示的字符串信息
     */
    protected fun showToast(str: String) {
        StrUtil.showToast(this, str)
    }

    /**
     * 显示提示信息
     * @param resId 要显示的字符串在strings文件中的ID
     */
    protected fun showToast(@StringRes resId: Int) {
        StrUtil.showToast(this, resId)
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
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    /**
     * 带参数启动新Activity
     * @param cls 要启动的Activity类，格式为 Activity名::class.java
     * @param objects 向Activity传递的参数，奇数项为键，偶数项为值
     */
    protected fun startActivity(cls: Class<*>, vararg objects: String) {
        val intent = Intent(this, cls)
        var i = 0
        while (i < objects.size) {
            intent.putExtra(objects[i], objects[++i])
            i++
        }
        startActivity(intent)
    }


    /**
     * 检查系统是否授予权限
     *
     * @param permission 需要的权限 Manifest.permission.权限名
     * @param requestId  请求ID
     */
    fun checkLocalPhonePermission(permission: String, requestId: Int): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            // 是否已授权
            val permissionFlag = ActivityCompat.checkSelfPermission(this, permission)
            if (permissionFlag != PackageManager.PERMISSION_GRANTED) {
                // 若未授权则请求授权
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestId)
                return false
            }
        }
        return true
    }

    /**
     * 检查定制系统(小米)是否授予权限
     *
     * @param opsPermission 定制系统权限名 AppOpsManager.权限名
     * @param permission    需要的权限 Manifest.permission.权限名
     * @param requestId     请求ID
     */
    fun checkOpsPermission(opsPermission: String, permission: String, requestId: Int): Boolean {
        if (Build.VERSION.SDK_INT >= 19) {
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            // 是否已授权
            val permissionFlag = appOpsManager
                    .checkOp(opsPermission, Binder.getCallingUid(), packageName)
            if (permissionFlag != AppOpsManager.MODE_ALLOWED) {
                // 若未授权则请求授权
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestId)
                return false
            }
        }
        return true
    }
}
