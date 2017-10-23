package com.xmx.tango.utils

import android.os.Handler
import java.util.*

/**
 * Created by The_onE on 2016/3/23.
 * 自定义定时器，实现随时开始、停止，或立即执行
 * 在新线程中执行
 * @property timer 定时器执行的操作
 */
class Timer(private val timer: () -> Unit) {

    companion object {
        private val map = HashMap<Any, MutableList<Timer>>() // 定时器与对象关联

        /**
         * 释放对象，关闭所有与对象关联的定时器
         * @param obj 释放的对象
         */
        fun release(obj: Any) {
            val list = map[obj]
            if (list != null) {
                for (item in list) {
                    item.stop()
                }
            }
            map.remove(obj)
        }
    }

    // 延迟时间，需自定义
    private var delay: Long = 1000
    // 是否只执行一次
    private var onceFlag: Boolean = false
    // 执行关联的对象
    private var obj: Any? = null

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
     * @param[o] 关联的对象
     */
    fun start(d: Long, once: Boolean = false, o: Any? = null) {
        delay = d
        onceFlag = once
        handler.postDelayed(runnable, delay)
        // 关联对象
        if (o != null) {
            obj = o
            var list = map[o]
            if (list == null) {
                list = LinkedList()
                map[o] = list
            }
            list.add(this)
        }
    }

    /**
     * 结束计时，不再执行
     */
    fun stop() {
        handler.removeCallbacks(runnable)
        // 取消与对象的关联
        obj?.apply {
            map[this]?.removeAll { it == this }
        }
    }

    /**
     * 立即执行一次，不影响计时
     */
    fun execute() {
        timer()
    }
}
