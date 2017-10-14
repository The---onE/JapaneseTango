package com.xmx.tango.common.data.sql

/**
 * Created by The_onE on 2016/1/11.
 * 批量删除回调
 */
abstract class DeleteCallback {

    /**
     * 每次删除后执行的操作
     * @param index 已删除的数量
     */
    abstract fun proceeding(index: Int)

    /**
     * 全部删除成功后执行的操作
     * @param total 删除的总数
     */
    abstract fun success(total: Int)

    /**
     * 中途失败的操作
     * @param e 异常信息
     */
    abstract fun error(e: Exception)
}
