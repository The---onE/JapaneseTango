package com.xmx.tango.module.calendar

import android.content.ContentValues
import android.database.Cursor

import com.xmx.tango.common.data.sql.ISqlEntity

import java.util.Date

/**
 * Created by The_onE on 2016/9/13.
 * 打卡日期数据实体
 */
class DateData : ISqlEntity {
    var id = -1L
    var year = 0
    var month = 0
    var date = 0
    var checkIn = 0
    var study = 0
    var review = 0
    var mission = 0
    var addTime: Date = Date(0)
    private var flags = ""
    private var delFlag = 0

    override fun tableFields(): String {
        return "ID integer not null primary key autoincrement, " +
                "Year integer not null, " +
                "Month integer not null, " +
                "Date integer not null, " +
                "CheckIn integer not null default(-1), " +
                "Study integer not null default(0), " +
                "Review integer not null default(0), " +
                "Mission integer not null default(0), " +
                "AddTime integer not null default(0), " +
                "Flags text, " +
                "DelFlag integer not null default(0)"
    }

    override fun getContent(): ContentValues {
        val content = ContentValues()
        if (id > 0) {
            content.put("ID", id)
        }
        content.put("Year", year)
        content.put("Month", month)
        content.put("Date", date)
        content.put("CheckIn", checkIn)
        content.put("Study", study)
        content.put("Review", review)
        content.put("Mission", mission)
        content.put("AddTime", addTime.time)
        content.put("Flags", flags)
        content.put("DelFlag", delFlag)
        return content
    }

    override fun convertToEntity(c: Cursor): DateData {
        val entity = DateData()
        entity.id = c.getLong(0)
        entity.year = c.getInt(1)
        entity.month = c.getInt(2)
        entity.date = c.getInt(3)
        entity.checkIn = c.getInt(4)
        entity.study = c.getInt(5)
        entity.review = c.getInt(6)
        entity.mission = c.getInt(7)
        entity.addTime = Date(c.getLong(8))
        entity.flags = c.getString(9)?: ""
        entity.delFlag = c.getInt(10)

        return entity
    }
}
