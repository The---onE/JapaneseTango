package com.xmx.tango.common.log

import android.content.ContentValues
import android.database.Cursor

import com.xmx.tango.common.data.sql.ISqlEntity

import java.util.Date

/**
 * Created by The_onE on 2016/9/4.
 * 操作日志实体
 */
class OperationLog : ISqlEntity {
    private var mId: Long = -1
    var mOperation: String? = null
    var mTime = Date()

    override fun tableFields(): String {
        return "ID integer not null primary key autoincrement, " +
                "Operation text, " +
                "Time integer not null default(0)"
    }

    override fun getContent(): ContentValues {
        val content = ContentValues()
        if (mId > 0) {
            content.put("ID", mId)
        }
        content.put("Operation", mOperation)
        content.put("Time", mTime.time)
        return content
    }

    override fun convertToEntity(c: Cursor): OperationLog {
        val entity = OperationLog()
        entity.mId = c.getLong(0)
        entity.mOperation = c.getString(1)
        entity.mTime = Date(c.getLong(2))

        return entity
    }
}
