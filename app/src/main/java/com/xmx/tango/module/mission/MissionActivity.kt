package com.xmx.tango.module.mission

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.xmx.tango.R
import com.xmx.tango.module.font.JapaneseFontChangeEvent
import com.xmx.tango.module.operate.LoadNewTangoEvent
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.module.operate.TangoOperator
import com.xmx.tango.module.verb.VerbDialog
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.utils.Timer
import com.xmx.tango.utils.VibratorUtil
import kotlinx.android.synthetic.main.activity_mission.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.util.Random

/**
 * Created by The_onE on 2017/9/30.
 * 任务模式Activity
 */
class MissionActivity : BaseTempActivity() {
    private var tango: Tango? = null // 当前单语
    private var prevTango: Tango? = null // 上一个单语
    private var prevOperate: Int = 0 // 上一次操作
    private var totalTango: Int = 0 // 本次任务单语总数
    private var random = Random() // 生成随机数

    private var operateFlag = true // 是否可以操作

    companion object {
        private val REMEMBER = 1 // 记住了
        private val FORGET = 2 // 没记住
        private val REMEMBER_FOREVER = 3 // 彻底记住
    }

    // 定时显示发音
    private var pronunciationTimer = Timer {
        showPronunciation()
        checkAnswer()
    }
    private var pronunciationFlag = false // 是否已显示发音

    // 定时显示写法
    private var writingTimer = Timer {
        showWriting()
        checkAnswer()
    }
    private var writingFlag = false // 是否已显示写法

    // 定时显示解释
    private var meaningTimer = Timer {
        showMeaning()
        checkAnswer()
    }
    private var meaningFlag = false // 是否已显示解释

    /**
     * 操作单语
     */
    @SuppressLint("SetTextI18n")
    private fun operateTango(operation: Int): Boolean {
        if (operateFlag) {
            // 可以操作则暂时不能操作
            operateFlag = false
            rememberButton.setBackgroundColor(Color.LTGRAY)
            forgetButton.setBackgroundColor(Color.LTGRAY)
            // 记录为上一次操作
            prevOperate = operation
            tango?.apply {
                if (this.id > 0) {
                    // 可以操作
                    when (operation) {
                        REMEMBER -> {
                            // 记住单语
                            TangoOperator.remember(tango)
                            TangoManager.removeFromWaitingList(tango)
                            // 震动提示
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(this@MissionActivity,
                                        TangoConstants.REMEMBER_VIBRATE_TIME)
                            }
                        }
                        FORGET -> {
                            // 没记住单语
                            TangoOperator.forget(tango)
                            // 震动提示
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(this@MissionActivity,
                                        TangoConstants.FORGET_VIBRATE_TIME)
                            }
                        }
                        REMEMBER_FOREVER -> {
                            // 彻底记住单语
                            TangoOperator.rememberForever(tango)
                            TangoManager.removeFromWaitingList(tango)
                            // 震动提示
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(this@MissionActivity,
                                        TangoConstants.REMEMBER_FOREVER_VIBRATE_TIME)
                            }
                        }
                    }
                    // 获取任务列表剩余单语数
                    val count = TangoManager.waitingList.size
                    if (count <= 0) {
                        showToast("任务完成！")
                        DataManager.todayMission = DataManager.todayMission + 1
                        finish()
                    }
                    countView!!.text = "任务剩余：$count\n任务已记：${totalTango - count}"

                    loadNew()
                    return true
                }
            }
            loadNew()
            return false
        } else {
            return false
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_mission)
    }

    @SuppressLint("SetTextI18n")
    override fun setListener() {
        // 记住了
        rememberButton.setOnClickListener { operateTango(REMEMBER) }
        // 没记住
        forgetButton.setOnClickListener { operateTango(FORGET) }
        // 显示答案
        answerButton.setOnClickListener { showAnswer() }
        // 点击写法朗读单语
        writingView.setOnClickListener {
            val writing = writingView.text.toString()
            if (writing != "") {
                SpeakTangoManager.speak(this@MissionActivity, writing)
            }
        }
        // 点击发音朗读单语
        pronunciationView.setOnClickListener {
            val pronunciation = pronunciationView!!.text.toString()
            if (pronunciation != "") {
                SpeakTangoManager.speak(this@MissionActivity, pronunciation)
            }
        }
        // 点击词性，若为动词则显示动词变形对话框
        partView.setOnClickListener {
            val part = partView.text.toString()
            if (part.isNotBlank()) {
                if (part.contains(TangoConstants.VERB_FLAG)) {
                    // 若为动词
                    tango?.apply {
                        val verb = this.writing
                        val type = when (this.partOfSpeech) {
                            TangoConstants.VERB1_FLAG -> 1
                            TangoConstants.VERB2_FLAG -> 2
                            TangoConstants.VERB3_FLAG -> 3
                            else -> 0
                        }
                        // 显示动词变形对话框
                        if (type > 0) {
                            val dialog = VerbDialog()
                            dialog.initDialog(this@MissionActivity, verb, type)
                            dialog.show(fragmentManager, "VERB")
                        }
                    }
                }
            }
        }
        // 点击上一个单语，撤销上次操作
        prevView.setOnClickListener {
            prevTango?.apply {
                // 撤销上次操作
                TangoOperator.cancelOperate()
                // 将上一个单语作为当前单语重新显示
                tango = null
                loadNewTango(this)
                TangoManager.addToWaitingList(tango)
                val count = TangoManager.waitingList.size
                countView!!.text = "任务剩余：$count\n任务已记：${totalTango - count}"
            }
        }
        // 按住记住了按钮向上滑动
        rememberButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    val wm = applicationContext
                            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val p = Point()
                    wm.defaultDisplay.getSize(p)
                    val h = p.y
                    val y = h - motionEvent.rawY
                    // 向上滑动超过屏幕高度1/3，永久记住单语
                    if (y > h / 3) {
                        operateTango(REMEMBER_FOREVER)
                    }
                }
            }
            false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun processLogic(savedInstanceState: Bundle?) {
        // 设置日文字体
        setJapaneseFont()
        // 根据学习目标获取任务列表
        val goal = DataManager.tangoGoal
        val reviewFlag = TangoOperator.study >= goal
        TangoManager.updateWaitingList(reviewFlag,
                DataManager.reviewFrequency,
                DataManager.missionCount)
        // 获取任务列表单语数
        val count = TangoManager.waitingList.size
        if (count <= 0) {
            showToast("没有符合条件的任务")
            finish()
        }
        totalTango = count
        countView.text = "任务剩余：$count\n任务已记：${totalTango - count}"
        // 加载新单语
        loadNewTango()
        // 注册事件监听
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onBackPressed() {
        // 显示提示对话框
        val builder = AlertDialog.Builder(this)
        builder.setMessage("任务还未完成，确认要退出吗？")
                .setTitle("提示")
                .setPositiveButton("确认") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 首页重新加载
        EventBus.getDefault().post(LoadNewTangoEvent())
    }

    /**
     * 检查信息是否全部显示，若全部显示则隐藏显示答案按钮
     */
    private fun checkAnswer() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            answerLine.visibility = View.GONE
            answerButton.isEnabled = false
            val animator = ObjectAnimator.ofFloat(answerButton, "alpha", 1f, 0f) // 渐出效果
            animator.duration = 300
            animator.start()
        }
    }

    /**
     * 渐变显示文本
     * @param tv 要显示的文本框
     */
    private fun showTextView(tv: TextView) {
        tv.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f) // 渐入效果
        animator.duration = 300
        animator.start()
    }

    /**
     * 显示发音
     */
    private fun showPronunciation() {
        showTextView(pronunciationView)
        showTextView(toneView)
        pronunciationFlag = true
    }

    /**
     * 延迟显示发音
     */
    private fun delayPronunciation() {
        pronunciationTimer.stop()
        pronunciationTimer.start((DataManager.pronunciationTime * 1000).toLong(), true)
    }

    /**
     * 显示写法
     */
    private fun showWriting() {
        showTextView(writingView)
        writingFlag = true
    }

    /**
     * 延迟显示写法
     */
    private fun delayWriting() {
        writingTimer.stop()
        writingTimer.start((DataManager.writingTime * 1000).toLong(), true)
    }

    /**
     * 显示解释
     */
    private fun showMeaning() {
        showTextView(partView)
        showTextView(meaningView)
        meaningFlag = true
    }

    /**
     * 延迟显示解释
     */
    private fun delayMeaning() {
        meaningTimer.stop()
        meaningTimer.start((DataManager.meaningTime * 1000).toLong(), true)
    }

    /**
     * 加载新单语
     */
    private fun loadNew() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            // 答案已显示，则直接加载新单语
            loadNewTango()
        } else {
            // 答案未显示，则显示答案，并延迟加载新单语
            showAnswer()
            Timer {
                loadNewTango()
            }.start(TangoConstants.NEW_TANGO_DELAY, true)
        }
    }

    /**
     * 测量文本宽度
     * @param textView 待测量的文本
     */
    private fun measureWidth(textView: TextView): Float =
            textView.paint.measureText(textView.text.toString())

    /**
     * 加载新单语
     */
    private fun loadNewTango() {
        // 根据目标随机选取新单语
        val goal = DataManager.tangoGoal
        val reviewFlag = TangoOperator.study >= goal
        val temp = TangoManager.randomTango(reviewFlag,
                DataManager.reviewFrequency, tango, true)
        temp?.apply { loadNewTango(this) }
    }

    /**
     * 加载新单语
     * @param newTango 要加载的单语
     */
    @SuppressLint("SetTextI18n")
    private fun loadNewTango(newTango: Tango) {
        // 初始化单语状态
        pronunciationFlag = false
        writingFlag = false
        meaningFlag = false
        // 显示答案按钮
        answerLine.visibility = View.VISIBLE
        answerButton.isEnabled = true
        val animator = ObjectAnimator.ofFloat(answerButton, "alpha", 0f, 1f) // 渐入效果
        animator.duration = 300
        animator.start()
        // 恢复按钮状态
        Timer {
            operateFlag = true
            rememberButton.setBackgroundColor(Color.TRANSPARENT)
            forgetButton.setBackgroundColor(Color.TRANSPARENT)
        }.start(TangoConstants.INTERVAL_TIME_MIN, true)
        // 记录上一个单语
        if (tango != null) {
            prevTango = tango
            showPrevTango()
        } else {
            prevView!!.text = ""
        }
        tango = newTango

        // 测量屏幕宽度用于调整文字大小
        val wm = applicationContext
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getSize(p)
        val width = p.x
        // 处理发音
        var textSize = TangoConstants.DEFAULT_PRONUNCIATION_TEXT_SIZE
        pronunciationView.textSize = textSize.toFloat()
        pronunciationView.text = newTango.pronunciation
        pronunciationView.visibility = View.INVISIBLE
        toneView.textSize = textSize.toFloat()
        if (newTango.tone >= 0 && newTango.tone < TangoConstants.TONES.size) {
            toneView.text = TangoConstants.TONES[newTango.tone]
        } else {
            toneView.text = ""
        }
        toneView.visibility = View.INVISIBLE
        var pronunciationLength = measureWidth(pronunciationView) + measureWidth(toneView)
        while (pronunciationLength > width) {
            textSize -= 1
            pronunciationView.textSize = textSize.toFloat()
            toneView.textSize = textSize.toFloat()
            pronunciationLength = measureWidth(pronunciationView) + measureWidth(toneView)
        }
        // 处理写法
        textSize = TangoConstants.DEFAULT_WRITING_TEXT_SIZE
        writingView!!.textSize = textSize.toFloat()
        writingView.text = newTango.writing
        writingView.visibility = View.INVISIBLE
        var writingLength = measureWidth(writingView)
        while (writingLength > width) {
            textSize -= 1
            writingView.textSize = textSize.toFloat()
            writingLength = measureWidth(writingView)
        }
        // 处理解释
        textSize = TangoConstants.DEFAULT_MEANING_TEXT_SIZE
        partView.textSize = textSize.toFloat()
        if (newTango.partOfSpeech != "") {
            partView.text = "[${newTango.partOfSpeech}]"
        } else {
            partView.text = ""
        }
        partView.visibility = View.INVISIBLE
        meaningView.textSize = TangoConstants.DEFAULT_MEANING_TEXT_SIZE.toFloat()
        meaningView.text = newTango.meaning
        meaningView.visibility = View.INVISIBLE
        var meaningLength = measureWidth(meaningView) + measureWidth(partView)
        while (meaningLength > width) {
            textSize -= 1
            partView.textSize = textSize.toFloat()
            meaningView.textSize = textSize.toFloat()
            meaningLength = measureWidth(meaningView) + measureWidth(partView)
        }

        // 随机选择给出哪个提示，其它延迟显示
        val i = random.nextInt(3)
        val r = random.nextBoolean()
        if (newTango.pronunciation.isBlank()) {
            // 发音为空
            showPronunciation()
            if (r) {
                showWriting()
                delayMeaning()
            } else {
                delayWriting()
                showMeaning()
            }
        } else if (newTango.writing.isBlank()) {
            // 写法为空
            showWriting()
            if (r) {
                showPronunciation()
                delayMeaning()
            } else {
                delayPronunciation()
                showMeaning()
            }
        } else if (newTango.meaning.isBlank()) {
            // 解释为空
            showMeaning()
            if (r) {
                showPronunciation()
                delayWriting()
            } else {
                delayPronunciation()
                showWriting()
            }
        } else if (newTango.pronunciation == newTango.writing) {
            // 发音与写法一致
            if (r) {
                showPronunciation()
                showWriting()
                delayMeaning()
            } else {
                // 发音与写法一起显示
                val pTime = DataManager.pronunciationTime.toInt() * 1000
                val wTime = DataManager.writingTime.toInt() * 1000
                val time = Math.min(pTime, wTime)
                pronunciationTimer.stop()
                pronunciationTimer.start(time.toLong(), true)

                writingTimer.stop()
                writingTimer.start(time.toLong(), true)
                showMeaning()
            }
        } else {
            // 信息齐全，随机显示
            when (i) {
                0 -> {
                    showPronunciation()
                    delayWriting()
                    delayMeaning()
                }

                1 -> {
                    delayPronunciation()
                    showWriting()
                    delayMeaning()
                }

                2 -> {
                    delayPronunciation()
                    delayWriting()
                    showMeaning()
                }

                else -> {
                    showPronunciation()
                    showWriting()
                    showMeaning()
                }
            }
        }
    }

    /**
     * 显示上一个单语信息
     */
    private fun showPrevTango() {
        prevTango?.apply {
            var text = ""
            when (prevOperate) {
                REMEMBER -> text = "√ "
                FORGET -> text = "× "
                REMEMBER_FOREVER -> text = "√ "
            }
            text += this.pronunciation + "\n"
            if (this.writing != this.pronunciation) {
                text += this.writing + "\n"
            }
            if (this.partOfSpeech != "") {
                text += "[" + this.partOfSpeech + "]"
            }
            text += this.meaning
            prevView.text = text
        }
    }

    /**
     * 显示答案
     */
    private fun showAnswer() {
        if (!pronunciationFlag) {
            pronunciationTimer.execute()
            pronunciationTimer.stop()
        }

        if (!writingFlag) {
            writingTimer.execute()
            writingTimer.stop()
        }

        if (!meaningFlag) {
            meaningTimer.execute()
            meaningTimer.stop()
        }
    }

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        // 获取保存的字体设置
        val title = DataManager.japaneseFontTitle
        val font = TangoConstants.JAPANESE_FONT_MAP[title]
        // 获取设置的字体
        val mgr = assets
        var tf = Typeface.DEFAULT
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font)
        }
        // 为日文设置字体
        pronunciationView.typeface = tf
        writingView.typeface = tf
    }

    /**
     * 处理加载新单语事件
     */
    @Subscribe
    fun onEvent(event: LoadNewTangoEvent) {
        loadNewTango()
    }

    /**
     * 处理日文字体更改事件
     */
    @Subscribe
    fun onEvent(event: JapaneseFontChangeEvent) {
        setJapaneseFont()
    }
}
