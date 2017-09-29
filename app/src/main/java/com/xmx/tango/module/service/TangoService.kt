package com.xmx.tango.module.service

import android.content.Intent

import com.xmx.tango.base.service.BaseService
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.core.activity.MainActivity
import com.xmx.tango.module.operate.TangoOperator
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.utils.Timer

class TangoService : BaseService() {

    private var timer: Timer? = null
    private var tango: Tango? = null

    override fun processLogic(intent: Intent) {
        // 开启定时器，定时加载新单语
        timer = object : Timer() {
            override fun timer() {
                loadNewTango()
            }
        }
        timer?.start(DataManager.getInstance().serviceInterval.toLong())
    }

    override fun setForeground(intent: Intent) {
        // 加载新单语，显示在通知栏
        loadNewTango()
    }

    /**
     * 加载新单语
     */
    private fun loadNewTango() {
        val goal = DataManager.getInstance().tangoGoal
        val reviewFlag = TangoOperator.getInstance().study >= goal
        // 随机选取单语
        val temp = TangoManager.getInstance().randomTango(reviewFlag,
                DataManager.getInstance().reviewFrequency, tango, false)
        loadNewTango(temp)
    }

    /**
     * 处理新单语
     * @param newTango 选取的单语
     */
    private fun loadNewTango(newTango: Tango) {
        tango = newTango
        tango?.apply {
            // 设置在通知栏显示单语信息
            val word = "${this.writing}(${this.pronunciation})"
            val meaning = "[${this.partOfSpeech}]${this.meaning}"
            showForeground(MainActivity::class.java, word, meaning)
        }
    }
}
