package com.xmx.tango.module.imp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask

import com.xmx.tango.core.activity.MainActivity
import com.xmx.tango.common.data.sql.InsertCallback
import com.xmx.tango.common.notification.NotificationUtils
import com.xmx.tango.base.service.BaseService
import com.xmx.tango.module.tango.TangoEntityManager
import com.xmx.tango.module.crud.TangoListChangeEvent
import com.xmx.tango.utils.ExceptionUtil

import org.greenrobot.eventbus.EventBus

/**
 * Created by The_onE on 2017/10/9.
 * 通过CSV文件导入单语Service
 */
class ImportFileService : BaseService() {
    var importNum: Int = 0

    @SuppressLint("StaticFieldLeak")
    override fun processLogic(intent: Intent) {
        // 获取要导入的CSV文件
        val path = intent.getStringExtra("path")
        val type = intent.getStringExtra("type")

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                // 解析CSV文件
                val records = CsvUtil.parseFile(path)
                // 生成单语实体列表
                val tangoList = ImportUtil.convertTangoList(records, type)
                // 总单语数
                val total = tangoList.size
                // 间隔多少单语在状态栏提示一次
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
                        // 记录导入成功数
                        importNum = total
                    }

                    override fun error(e: Exception) {
                        ExceptionUtil.normalException(e)
                    }
                })
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                // 导入成功
                // 在状态栏显示信息，点击进入主Activity
                val i = Intent(this@ImportFileService, MainActivity::class.java)
                NotificationUtils.showNotification(baseContext, 0, i, "日词",
                        "成功导入 $importNum 条数据")
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
