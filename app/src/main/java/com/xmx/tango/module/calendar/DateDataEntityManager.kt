package com.xmx.tango.module.calendar

import com.xmx.tango.common.data.sql.BaseSqlEntityManager

/**
 * Created by The_onE on 2016/9/13.
 * 打卡日期数据管理器
 */
object DateDataEntityManager : BaseSqlEntityManager<DateData>() {

    init {
        tableName = "DateData" // 表名
        entityTemplate = DateData() // 模版实体
        openDatabase() // 链接数据库
    }
}
