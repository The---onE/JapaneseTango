package com.xmx.tango.common.net

/**
 * Created by The_onE on 2017/5/26.
 * Get请求回调
 */
abstract class HttpGetCallback {
    abstract fun success(result: String)

    abstract fun fail(e: Exception)
}
