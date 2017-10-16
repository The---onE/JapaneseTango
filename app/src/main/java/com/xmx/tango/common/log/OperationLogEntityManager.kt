package com.xmx.tango.common.log

import com.xmx.tango.common.data.sql.BaseSQLEntityManager

import org.greenrobot.eventbus.EventBus

import java.util.Date

/**
 * Created by The_onE on 2016/9/4.
 * 操作日志实体管理器
 */
object OperationLogEntityManager : BaseSQLEntityManager<OperationLog>() {

    init {
        tableName = "OperationLog"
        entityTemplate = OperationLog()
        openDatabase(null)
    }

    /**
     * 添加日志
     * @param operation 操作信息
     */
    fun addLog(operation: String) {
        val entity = OperationLog()
        entity.mOperation = operation
        entity.mTime = Date()
        insertData(entity)
        EventBus.getDefault().post(LogChangeEvent())
    }
}
