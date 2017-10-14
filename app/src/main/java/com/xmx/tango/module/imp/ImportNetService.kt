package com.xmx.tango.module.imp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import com.xiaoleilu.hutool.exceptions.ExceptionUtil

import com.xmx.tango.base.service.BaseService
import com.xmx.tango.common.data.sql.InsertCallback
import com.xmx.tango.common.notification.NotificationUtils
import com.xmx.tango.core.activity.MainActivity
import com.xmx.tango.module.crud.TangoListChangeEvent
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoEntityManager

import org.greenrobot.eventbus.EventBus

/**
 * Created by The_onE on 2017/10/9.
 * 导入单语实体Service
 */
class ImportNetService : BaseService() {

    @SuppressLint("StaticFieldLeak")
    override fun processLogic(intent: Intent) {
        // 获取要导入的单语列表
        val tangoList = intent.getParcelableArrayListExtra<Tango>("list")

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                val total = tangoList.size
                val hint = if (total > 100) total / 100 else 1
                // 向数据库添加数据
                TangoEntityManager.insertData(tangoList, object : InsertCallback() {
                    override fun proceeding(index: Int) {
                        // 在状态栏提示导入进度
                        if (index % hint == 0) {
                            showForeground(MainActivity::class.java, "正在导入:$index/$total")
                        }
                    }

                    override fun success(total: Int) {
                        // 在状态栏显示信息，点击进入主Activity
                        val i = Intent(this@ImportNetService, MainActivity::class.java)
                        NotificationUtils.showNotification(baseContext, i, 0, "日词",
                                "成功导入 $total 条数据")
                    }

                    override fun error(e: Exception) {
                        filterException(e)
                    }
                })
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                // 导入成功
                showToast("导入成功")
                EventBus.getDefault().post(TangoListChangeEvent())
                // 关闭服务
                stopSelf()
            }
        }.execute()
    }

    override fun setForeground(intent: Intent) {
        // 设置状态栏显示信息
        showForeground(MainActivity::class.java, "正在导入")
    }
}
