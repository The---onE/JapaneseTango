package com.xmx.tango.core.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText

import com.xmx.tango.R
import com.xmx.tango.module.font.JapaneseFontDialog
import com.xmx.tango.module.font.JapaneseFontChangeEvent
import com.xmx.tango.module.operate.LoadNewTangoEvent
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.TangoConstants
import kotlinx.android.synthetic.main.activity_setting.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by The_onE on 2016/9/17.
 * 设置Activity
 */
class SettingActivity : BaseTempActivity() {

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_setting)
        // 设置学习的单语类型
        var type = DataManager.tangoType
        if (type.isBlank()) {
            type = "全部"
        }
        typeView.text = type
        // 设置学习的单语词性
        var part = DataManager.partOfSpeech
        if (part.isBlank()) {
            part = "全部"
        }
        partView.text = part
        // 设置每日学习目标
        goalView.text = "${DataManager.tangoGoal}"
        // 设置发音显示延迟时间
        pronunciationTimeView.text = "${DataManager.pronunciationTime}"
        // 设置写法显示延迟时间
        writingTimeView.text = "${DataManager.writingTime}"
        // 设置解释显示延迟时间
        meaningTimeView.text = "${DataManager.meaningTime}"
        // 设置复习系数
        frequencyView.text = "${DataManager.reviewFrequency}"
        // 设置任务模式单语数
        missionView.text = "${DataManager.missionCount}"
        // 设置日文字体
        japaneseFontView.text = "あいうえお 日本語"
        val title = DataManager.japaneseFontTitle
        val font = TangoConstants.JAPANESE_FONT_MAP[title]
        val mgr = assets
        var tf = Typeface.DEFAULT
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font)
        }
        japaneseFontView.typeface = tf
        // 设置朗读音色
        speakView.text = DataManager.tangoSpeaker
        // 设置震动模式
        val vibratorFlag = DataManager.vibratorStatus
        vibratorView.text = if (vibratorFlag) "开启" else "关闭"
        // 设置背词服务换词间隔
        serviceIntervalView.text = "${DataManager.serviceInterval}"
    }

    @SuppressLint("SetTextI18n")
    override fun setListener() {
        // 修改学习的单语类型
        layoutType.setOnClickListener {
            val typeEdit = EditText(this)
            typeEdit.setTextColor(Color.BLACK)
            typeEdit.textSize = 24f
            typeEdit.setText(DataManager.tangoType)
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("学习的単語类型")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(typeEdit)
                    .setPositiveButton("确定") { _, _ ->
                        var type = typeEdit.text.toString().trim()
                        DataManager.tangoType = type
                        if (type.isBlank()) {
                            type = "全部"
                        }
                        typeView.text = type
                        showToast("更改成功")
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改学习的单语词性
        layoutPart.setOnClickListener {
            val partEdit = EditText(this)
            partEdit.setTextColor(Color.BLACK)
            partEdit.textSize = 24f
            partEdit.setText(DataManager.partOfSpeech)
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("学习的単語词性")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(partEdit)
                    .setPositiveButton("确定") { _, _ ->
                        var part = partEdit.text.toString().trim()
                        DataManager.partOfSpeech = part
                        if (part.isBlank()) {
                            part = "全部"
                        }
                        partView.text = part
                        showToast("更改成功")
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改每日的学习目标
        layoutGoal.setOnClickListener {
            val goalEdit = EditText(this)
            goalEdit.setTextColor(Color.BLACK)
            goalEdit.textSize = 24f
            goalEdit.inputType = InputType.TYPE_CLASS_NUMBER
            goalEdit.setText("${DataManager.tangoGoal}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("每日的学习目标")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(goalEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val goalString = goalEdit.text.toString()
                        val goal = if (goalString.isNotBlank()) {
                            Integer.parseInt(goalString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.tangoGoal = goal
                        showToast("更改成功")
                        goalView.text = "$goal"
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改发音延迟时间
        layoutPronunciationTime.setOnClickListener {
            val pronunciationTimeEdit = EditText(this)
            pronunciationTimeEdit.setTextColor(Color.BLACK)
            pronunciationTimeEdit.textSize = 24f
            pronunciationTimeEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            pronunciationTimeEdit.setText("${DataManager.pronunciationTime}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("发音延迟时间")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(pronunciationTimeEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val pronunciationTimeString = pronunciationTimeEdit.text.toString()
                        val pronunciationTime = if (pronunciationTimeString.isNotBlank()) {
                            java.lang.Float.parseFloat(pronunciationTimeString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.pronunciationTime = pronunciationTime
                        showToast("更改成功")
                        pronunciationTimeView.text = "$pronunciationTime"
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改写法延迟时间
        layoutWritingTime.setOnClickListener {
            val writingTimeEdit = EditText(this)
            writingTimeEdit.setTextColor(Color.BLACK)
            writingTimeEdit.textSize = 24f
            writingTimeEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            writingTimeEdit.setText("${DataManager.writingTime}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("写法延迟时间")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(writingTimeEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val writingTimeString = writingTimeEdit.text.toString()
                        val writingTime = if (writingTimeString.isNotBlank()) {
                            java.lang.Float.parseFloat(writingTimeString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.writingTime = writingTime
                        showToast("更改成功")
                        writingTimeView.text = "$writingTime"
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改解释延迟时间
        layoutMeaningTime.setOnClickListener {
            val meaningTimeEdit = EditText(this)
            meaningTimeEdit.setTextColor(Color.BLACK)
            meaningTimeEdit.textSize = 24f
            meaningTimeEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            meaningTimeEdit.setText("${DataManager.meaningTime}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("解释延迟时间")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(meaningTimeEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val meaningTimeString = meaningTimeEdit.text.toString()
                        val meaningTime = if (meaningTimeString.isNotBlank()) {
                            java.lang.Float.parseFloat(meaningTimeString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.meaningTime = meaningTime
                        showToast("更改成功")
                        meaningTimeView.text = "$meaningTime"
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改复习系数
        layoutFrequency.setOnClickListener {
            val frequencyEdit = EditText(this)
            frequencyEdit.setTextColor(Color.BLACK)
            frequencyEdit.textSize = 24f
            frequencyEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            frequencyEdit.setText("${DataManager.reviewFrequency}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("复习系数")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(frequencyEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val frequencyString = frequencyEdit.text.toString()
                        val frequency = if (frequencyString.isNotBlank()) {
                            Integer.parseInt(frequencyString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.reviewFrequency = frequency
                        showToast("更改成功")
                        frequencyView.text = "$frequency"
                        EventBus.getDefault().post(LoadNewTangoEvent())
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改任务模式单语数
        layoutMissionCount.setOnClickListener {
            val missionEdit = EditText(this)
            missionEdit.setTextColor(Color.BLACK)
            missionEdit.textSize = 24f
            missionEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            missionEdit.setText("${DataManager.missionCount}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("任务模式単語数")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(missionEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val missionString = missionEdit.text.toString()
                        val mission = if (missionString.isNotBlank()) {
                            Integer.parseInt(missionString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.missionCount = mission
                        showToast("更改成功")
                        missionView.text = "$mission"
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 修改日文字体
        layoutJapaneseFont.setOnClickListener {
            // 弹出修改字体对话框
            val dialog = JapaneseFontDialog()
            dialog.initDialog(this@SettingActivity)
            dialog.show(fragmentManager, "JAPANESE_FONT")
        }
        // 修改朗读音色
        layoutSpeaker.setOnClickListener {
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("朗读音色")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(TangoConstants.SPEAKERS) { _, i ->
                        DataManager.tangoSpeaker = TangoConstants.SPEAKERS[i]
                        speakView.text = TangoConstants.SPEAKERS[i]
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 朗读测试文本
        btnSpeak.setOnClickListener {
            val text = editSpeak.text.toString()
            SpeakTangoManager.speak(this@SettingActivity, text)
        }
        // 切换震动模式
        layoutVibrator.setOnClickListener {
            var vibratorFlag = DataManager.vibratorStatus
            vibratorFlag = !vibratorFlag
            DataManager.vibratorStatus = vibratorFlag
            vibratorView.text = if (vibratorFlag) "开启" else "关闭"
        }
        // 修改背词服务换词间隔
        layoutServiceInterval.setOnClickListener {
            val serviceIntervalEdit = EditText(this)
            serviceIntervalEdit.setTextColor(Color.BLACK)
            serviceIntervalEdit.textSize = 24f
            serviceIntervalEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            serviceIntervalEdit.setText("${DataManager.serviceInterval}")
            AlertDialog.Builder(this@SettingActivity)
                    .setTitle("服务换词间隔(ms)")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(serviceIntervalEdit)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        val serviceIntervalString = serviceIntervalEdit.text.toString()
                        val serviceInterval = if (serviceIntervalString.isNotBlank()) {
                            Integer.parseInt(serviceIntervalString)
                        } else {
                            showToast("更改失败")
                            return@OnClickListener
                        }
                        DataManager.serviceInterval = serviceInterval
                        showToast("更改成功")
                        serviceIntervalView.text = "$serviceInterval"
                    })
                    .setNegativeButton("取消", null)
                    .show()
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
    }

    @Subscribe
    fun onEvent(event: JapaneseFontChangeEvent) {
        // 设置日文字体
        val title = DataManager.japaneseFontTitle
        val font = TangoConstants.JAPANESE_FONT_MAP[title]
        var tf = Typeface.DEFAULT
        if (font != null) {
            tf = Typeface.createFromAsset(assets, font)
        }
        japaneseFontView.typeface = tf
    }
}
