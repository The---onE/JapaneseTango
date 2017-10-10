package com.xmx.tango.module.imp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.net.HttpGetCallback
import com.xmx.tango.common.net.HttpManager
import com.xmx.tango.module.net.NetConstants
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
            // 向服务器请求数据
            HttpManager.getInstance().get(NetConstants.TANGO_ENTITY_LIST_URL, condition, object : HttpGetCallback() {
                override fun success(result: String) {
                    // 获取成功
                    var res = result
                    view.isEnabled = true
                    res = res.trim()
                    if (res.startsWith("{")) {
                        try {
                            val map = JsonUtil.parseObject(res)
                            val status = map[JsonUtil.RESPONSE_STATUS] as String
                            if (JsonUtil.STATUS_QUERY_SUCCESS == status) {
                                showToast(map[JsonUtil.RESPONSE_PROMPT] as String)
                                AlertDialog.Builder(this@ImportNetActivity)
                                        .setTitle("导入")
                                        .setMessage("确定要导入吗")
                                        .setPositiveButton("导入") { _, _ ->
                                            val entities = map[JsonUtil.RESPONSE_ENTITIES] as List<*>
                                            val tangoList = entities.mapTo(ArrayList<Tango>()) {
                                                Tango.convertFromJson(it as Map<String, Any>)
                                            }
                                            showToast("正在导入，请稍后")
                                            val service = Intent(this@ImportNetActivity,
                                                    ImportNetService::class.java)
                                            service.putParcelableArrayListExtra("list",
                                                    tangoList)
                                            service.putExtra("type", type)
                                            startService(service)
                                        }
                                        .setNegativeButton("取消", null)
                                        .show()
                            } else {
                                showToast(map[JsonUtil.RESPONSE_PROMPT] as String)
                            }
                        } catch (e: Exception) {
                            ExceptionUtil.normalException(e, this@ImportNetActivity)
                            showToast("数据异常")
                        }
                    } else {
                        // 返回的数据不符合要求的JSON格式
                        showToast("服务器连接失败")
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
}
