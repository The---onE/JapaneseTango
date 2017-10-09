package com.xmx.tango.module.crud

import android.os.Bundle

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoEntityManager
import kotlinx.android.synthetic.main.activity_add_tango.*

import org.greenrobot.eventbus.EventBus

import java.util.Date

/**
 * Created by The_onE on 2017/10/8.
 * 添加单语Activity
 */
class AddTangoActivity : BaseTempActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_add_tango)
    }

    override fun setListener() {
        btnAddTango.setOnClickListener {
            val entity = Tango()
            // 写法
            val writing = writingView.text.toString().trim()
            entity.writing = writing
            // 发音
            val pronunciation = pronunciationView.text.toString().trim()
            entity.pronunciation = pronunciation
            // 解释
            entity.meaning = meaningView.text.toString().trim()
            // 音调
            val toneStr = toneView.text.toString().trim()
            var tone = -1
            if (toneStr.isNotBlank()) {
                try {
                    tone = Integer.parseInt(toneStr)
                } catch (e: Exception) {
                    showToast("音调格式不合法")
                    return@setOnClickListener
                }
            }
            entity.tone = tone
            // 词性
            val partOfSpeech = partOfSpeechView.text.toString().trim()
            entity.partOfSpeech = partOfSpeech
            // 类型
            val type = typeView.text.toString().trim()
            entity.type = type
            // 写法和发音不能同时为空
            if (writing == "" && pronunciation == "") {
                showToast(R.string.add_error)
                return@setOnClickListener
            }
            // 添加单语
            entity.addTime = Date()
            TangoEntityManager.insertData(entity)
            // 清空编辑框
            writingView.setText("")
            pronunciationView.setText("")
            meaningView.setText("")
            toneView.setText("")
            partOfSpeechView.setText("")
            typeView.setText("")

            showToast(R.string.add_success)

            EventBus.getDefault().post(TangoListChangeEvent())
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {

    }
}
