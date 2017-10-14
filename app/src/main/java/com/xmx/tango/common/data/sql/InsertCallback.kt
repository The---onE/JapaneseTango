package com.xmx.tango.common.data.sql

/**
 * Created by The_onE on 2016/1/11.
 * 批量插入回调
 */
abstract class InsertCallback {

    /**
     * 每次插入后执行的操作
     * @param index 已插入的数量
     */
    abstract fun proceeding(index: Int)

    /**
     * 全部插入成功后执行的操作
     * @param total 插入的总数
     */
    abstract fun success(total: Int)

    /**
     * 中途执行失败的操作
     * @param e 异常信息
     */
    abstract fun error(e: Exception)
}
