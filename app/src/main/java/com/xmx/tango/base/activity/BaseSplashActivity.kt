package com.xmx.tango.base.activity

import com.xmx.tango.core.activity.MainActivity

/**
 * Created by The_onE on 2016/10/8.
 * 启动Activity基类，APP启动页，预处理部分数据后跳转至内容页
 */
abstract class BaseSplashActivity : BaseActivity() {
    /**
     * 跳转至主Activity，结束自身
     */
    protected fun startMainActivity() {
        startActivity(MainActivity::class.java)
        finish()
    }
}
