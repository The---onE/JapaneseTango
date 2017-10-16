package com.xmx.tango.common.log

import java.util.ArrayList

/**
 * Created by The_onE on 2016/2/24.
 * 操作日志管理器
 */
object OperationLogManager {

    private var sqlVersion: Long = 0
    private var version = System.currentTimeMillis()
    private var sqlList: List<OperationLog>? = ArrayList()

    val data: List<OperationLog>
        get() = sqlList ?: ArrayList()

    /**
     * 更新数据
     * @return 当前版本
     */
    fun updateData(): Long {
        val logManager = OperationLogEntityManager
        if (logManager.version != sqlVersion) {
            sqlVersion = logManager.version

            sqlList = logManager.selectAll("Time", false)

            version++
        }
        return version
    }
}
