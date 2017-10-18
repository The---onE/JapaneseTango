package com.xmx.tango.utils

import android.os.Handler

/**
 * Created by The_onE on 2016/3/23.
 * 自定义定时器，实现随时开始、停止，或立即执行
 * 在新线程中执行
 * @property timer 定时器执行的操作
 */
class Timer(private val timer: () -> Unit) {
    // 延迟时间，需自定义
    private var delay: Long = 1000
    // 是否只执行一次
    private var onceFlag: Boolean = false

    private var handler = Handler()
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            // 执行操作
            timer()
            // 如果不是只执行一次，则延迟时间后再次执行
            if (!onceFlag) {
                handler.postDelayed(this, delay)
            }
        }
    }

    /**
     * 开始计时，d毫秒后执行一次，之后每d毫秒后执行一次
     * @param[d] 间隔事件
     * @param[once] 是否只执行一次
     */
    fun start(d: Long, once: Boolean = false) {
        delay = d
        onceFlag = once
        handler.postDelayed(runnable, delay)
    }

    /**
     * 结束计时，不再执行
     */
    fun stop() {
        handler.removeCallbacks(runnable)
    }

    /**
     * 立即执行一次，不影响计时
     */
    fun execute() {
        timer()
    }
}
