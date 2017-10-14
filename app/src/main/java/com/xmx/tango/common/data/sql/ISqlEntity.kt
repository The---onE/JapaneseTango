package com.xmx.tango.common.data.sql

import android.content.ContentValues
import android.database.Cursor

/**
 * Created by The_onE on 2016/3/22.
 * 可用于数据库存储的实体接口
 */
interface ISqlEntity {

    /**
     * 生成用于Insert实体的ContentValues
     */
    fun getContent(): ContentValues

    /**
     * 生成用于建表的字段信息列表
     */
    fun tableFields(): String

    /**
     * 将查询出的Cursor转化为实体对象
     */
    fun convertToEntity(c: Cursor): ISqlEntity
}