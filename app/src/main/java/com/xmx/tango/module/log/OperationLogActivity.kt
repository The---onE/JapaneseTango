package com.xmx.tango.module.log

import android.os.Bundle

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.log.LogChangeEvent
import com.xmx.tango.common.log.OperationLogEntityManager
import com.xmx.tango.common.log.OperationLogManager
import kotlinx.android.synthetic.main.activity_operation_log.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.util.ArrayList

/**
 * Created by The_onE on 2017/10/5.
 * 操作日志Activity
 */
class OperationLogActivity : BaseTempActivity() {

    // 操作日志列表适配器
    private var operationLogAdapter: OperationLogAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_operation_log)

        // 为操作日志列表生成适配器
        operationLogAdapter = OperationLogAdapter(this, ArrayList())
        sqlList.adapter = operationLogAdapter
    }

    override fun setListener() {
        btnClearLog.setOnClickListener {
            // 清空操作日志
            OperationLogEntityManager.clearDatabase()
            OperationLogManager.updateData()
            operationLogAdapter?.updateList(OperationLogManager.data)
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {
        // 获取操作日志
        OperationLogManager.updateData()
        operationLogAdapter?.updateList(OperationLogManager.data)
        // 注册事件监听
        EventBus.getDefault().register(this)
    }

    /**
     * 监听日志更改事件
     */
    @Subscribe
    fun onEvent(event: LogChangeEvent) {
        OperationLogManager.updateData()
        operationLogAdapter?.updateList(OperationLogManager.data)
    }
}
