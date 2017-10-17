package com.xmx.tango.core

import android.app.Activity
import android.app.Service
import android.support.multidex.MultiDexApplication

import com.xmx.tango.common.data.DataManager

import java.util.LinkedList

/**
 * Created by The_onE on 2016/1/3.
 * 自定义Application，应用唯一实例
 * 单例类，初始化各插件，管理各组件
 */
class MyApplication : MultiDexApplication() {
    // 单例模式
    companion object {
        private var ins: MyApplication? = null
        fun getInstance(): MyApplication = ins!!
    }

    // 运行中的Activity容器
    private val activityList = LinkedList<Activity>()
    // 运行中的Service容器
    val serviceList = LinkedList<Service>()

    /**
     * 添加Activity到容器中
     * @param activity 要添加的Activity
     */
    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    /**
     * 从容器中移除Activity
     * @param activity 要添加的Activity
     */
    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }

    /**
     * 添加Service到容器中
     * @param service 要添加的Service
     */
    fun addService(service: Service) {
        serviceList.add(service)
    }

    /**
     * 从容器中移除Service
     * @param service 要添加的Service
     */
    fun removeService(service: Service) {
        serviceList.remove(service)
    }

    /**
     * 退出程序，关闭所有相关组件
     */
    fun exit() {
        activityList.forEach { it.finish() }
        serviceList.forEach { it.stopSelf() }
        System.exit(0)
    }

    override fun onCreate() {
        super.onCreate()
        // 设置当前实例
        ins = this

        // 注册异常处理器
        CrashHandler.init(this)

        // 初始化数据管理器
        DataManager.setContext(this)
    }
}
