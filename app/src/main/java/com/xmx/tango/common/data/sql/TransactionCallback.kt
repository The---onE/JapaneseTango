package com.xmx.tango.common.data.sql

/**
 * Created by The_onE on 2016/11/9.
 * 数据库事务执行回调
 */

abstract class TransactionCallback {
    @Throws(Exception::class)
    /**
     * 事务中执行的操作
     * @return 更新条数，用于success函数
     */
    abstract fun operation(): Int

    /**
     * 事务执行成功的操作
     * @param total 更新条数
     */
    abstract fun success(total: Int)

    /**
     * 事务执行失败的操作
     * @param e 异常信息
     */
    abstract fun error(e: Exception)
}
