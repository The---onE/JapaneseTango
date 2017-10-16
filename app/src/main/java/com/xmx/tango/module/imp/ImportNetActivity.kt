package com.xmx.tango.module.imp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.alibaba.fastjson.JSON

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.net.HttpGetCallback
import com.xmx.tango.common.net.HttpManager
import com.xmx.tango.module.net.NetConstants
import com.xmx.tango.module.net.TinyTango
import com.xmx.tango.module.net.TinyTangoListResult
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.utils.ExceptionUtil
import com.xmx.tango.utils.JsonUtil
import kotlinx.android.synthetic.main.activity_import_net.*

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by The_onE on 2017/10/10.
 * 从网络导入单语Activity
 */
class ImportNetActivity : BaseTempActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_import_net)
        title = "网络导入"
    }

    override fun setListener() {
        // 点击导入
        btnImportNet.setOnClickListener { view ->
            // 设置从服务器筛选的类型
            val type = typeView.text.toString()
            val condition = HashMap<String, String>()
            if (type.isNotBlank()) {
                condition.put("type", type.trim())
            }
            // 暂时禁用按钮
            view.isEnabled = false

//            val tangoListResult = TinyTangoListResult()
//            val tangoList = ArrayList<TinyTango>()
//            tangoList.add(TinyTango("写法", "发音", "解释",
//                    1, "词性"))
//            val tinyTango = TinyTango()
//            tinyTango.writing = "写法"
//            tinyTango.meaning = "解释"
//            tangoList.add(tinyTango)
//            val result: String = tangoListResult
//                    .setStatus(JsonUtil.STATUS_QUERY_SUCCESS)
//                    .setPrompt("获取成功")
//                    .setList(tangoList).toJson()


            // 向服务器请求数据
            HttpManager.sendGet(NetConstants.TANGO_ENTITY_LIST_URL, condition,
                    object : HttpGetCallback() {
                        override fun success(result: String) {
                            // 获取成功
                            val res = result.trim()
                            view.isEnabled = true
                            try {
                                val listResult = JSON.parseObject(res, TinyTangoListResult::class.java)
                                when (listResult.status) {
                                    JsonUtil.STATUS_QUERY_SUCCESS -> {
                                        // 获取成功
                                        showToast(listResult.prompt)
                                        val list = listResult.list
                                        list?.apply {
                                            AlertDialog.Builder(this@ImportNetActivity)
                                                    .setTitle("获取的単語")
                                                    .setItems(convertStringList(list), null)
                                                    .setPositiveButton("导入") { _, _ ->
                                                        showToast("正在导入，请稍后")
                                                        val service = Intent(this@ImportNetActivity,
                                                                ImportNetService::class.java)
                                                        service.putParcelableArrayListExtra("list",
                                                                convertTangoList(list))
                                                        service.putExtra("type", type)
                                                        startService(service)
                                                    }
                                                    .setNegativeButton("取消", null)
                                                    .show()
                                        }
                                    }
                                    JsonUtil.STATUS_ERROR -> {
                                        // 获取失败
                                        showToast(listResult.prompt)
                                    }
                                }
                            } catch (e: Exception) {
                                ExceptionUtil.normalException(e, this@ImportNetActivity)
                                showToast("数据异常")
                            }
                        }

                        override fun fail(e: Exception) {
                            // 获取失败
                            showToast("服务器连接失败")
                            view.isEnabled = true
                        }
                    })
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {}

    private fun convertTangoList(tinyTangoList: List<TinyTango>): ArrayList<Tango> =
            tinyTangoList.mapTo(ArrayList()) { Tango.convertFromTinyTango(it) }

    private fun convertStringList(tinyTangoList: List<TinyTango>): Array<String> =
            tinyTangoList.mapTo(ArrayList()) {
                "${it.writing}:${it.pronunciation}" + "|${it.meaning}"
            }.toTypedArray()
}
