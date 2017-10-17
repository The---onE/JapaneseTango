package com.xmx.tango.core.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.xmx.tango.R
import com.xmx.tango.base.fragment.BaseFragment

import com.xmx.tango.module.crud.ChooseTangoEvent
import com.xmx.tango.module.font.JapaneseFontChangeEvent
import com.xmx.tango.module.operate.LoadNewTangoEvent
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.module.operate.TangoOperator
import com.xmx.tango.module.verb.VerbDialog
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.utils.Timer
import com.xmx.tango.utils.VibratorUtil
import kotlinx.android.synthetic.main.fragment_home.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * Created by The_onE on 2017/10/16.
 * 首页Fragment
 */
class HomeFragment : BaseFragment() {

    companion object {
        private val REMEMBER = 1 // 记住了
        private val FORGET = 2 // 没记住
        private val REMEMBER_FOREVER = 3 // 彻底记住
    }

    private var tango: Tango? = null // 当前单语
    private var prevTango: Tango? = null // 上一个单语
    private var prevOperate: Int = 0 // 上一次操作
    private var operateFlag = true // 当前是否可以操作
    private var random = Random() // 随机数生成器

    // 显示发音定时器
    private var pronunciationFlag = false
    private var pronunciationTimer = object : Timer() {
        override fun timer() {
            showPronunciation()
            checkAnswer()
        }
    }
    // 显示写法定时器
    private var writingFlag = false
    private var writingTimer = object : Timer() {
        override fun timer() {
            showWriting()
            checkAnswer()
        }
    }
    // 显示解释定时器
    private var meaningFlag = false
    private var meaningTimer = object : Timer() {
        override fun timer() {
            showMeaning()
            checkAnswer()
        }
    }

    /**
     * 操作单语
     * @param operation 操作
     * @return 是否操作成功
     */
    @SuppressLint("SetTextI18n")
    private fun operateTango(operation: Int): Boolean {
        if (operateFlag) {
            // 若为可以操作状态，阻止重复操作
            operateFlag = false
            // 设置按钮为灰色状态
            rememberButton.setBackgroundColor(Color.LTGRAY)
            forgetButton.setBackgroundColor(Color.LTGRAY)
            // 保存操作记录
            prevOperate = operation
            tango?.apply {
                if (this.id > 0) {
                    // 若非测试单语（数据库无数据时显示的单语）
                    when (operation) {
                        REMEMBER -> {
                            // 记住了
                            TangoOperator.remember(tango)
                            // 震动
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(context,
                                        TangoConstants.REMEMBER_VIBRATE_TIME)
                            }
                        }
                        FORGET -> {
                            // 没记住
                            TangoOperator.forget(tango)
                            // 震动
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(context,
                                        TangoConstants.FORGET_VIBRATE_TIME)
                            }
                        }
                        REMEMBER_FOREVER -> {
                            // 彻底记住
                            TangoOperator.rememberForever(tango)
                            // 震动
                            if (DataManager.vibratorStatus) {
                                VibratorUtil.vibrate(context,
                                        TangoConstants.REMEMBER_FOREVER_VIBRATE_TIME)
                            }
                        }
                    }
                    // 更新学习记录
                    countView.text = "今日复习：${TangoOperator.review}\n" +
                            "今日已记：${TangoOperator.study}\n" +
                            "今日完成任务：${DataManager.todayMission}"
                    // 加载新单语
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

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun initView(view: View, savedInstanceState: Bundle?) {
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun setListener(view: View) {
        // 记住了
        rememberButton.setOnClickListener {
            operateTango(REMEMBER)
        }
        // 没记住
        forgetButton.setOnClickListener {
            operateTango(FORGET)
        }
        // 显示答案
        answerButton.setOnClickListener {
            showAnswer()
        }
        // 点击写法，朗读单语
        writingView.setOnClickListener {
            val writing = writingView.text.toString()
            if (writing.isNotBlank()) {
                SpeakTangoManager.speak(context, writing)
            }
        }
        // 点击发音，朗读单语
        pronunciationView.setOnClickListener {
            val pronunciation = pronunciationView.text.toString()
            if (pronunciation.isNotBlank()) {
                SpeakTangoManager.speak(context, pronunciation)
            }
        }
        // 点击词性，若为动词弹出动词变形对话框
        partView.setOnClickListener {
            tango?.apply {
                val part = partView.text.toString()
                if (part.isNotBlank()) {
                    if (part.contains(TangoConstants.VERB_FLAG)) {
                        // 若为动词
                        val verb = this.writing
                        val type = when (this.partOfSpeech) {
                            TangoConstants.VERB1_FLAG -> 1
                            TangoConstants.VERB2_FLAG -> 2
                            TangoConstants.VERB3_FLAG -> 3
                            else -> 0
                        }
                        // 弹出动词变形对话框
                        if (type > 0) {
                            val dialog = VerbDialog()
                            dialog.initDialog(context, verb, type)
                            dialog.show(activity.fragmentManager, "VERB")
                        }
                    }
                }
            }
        }
        // 恢复上一次操作
        prevView.setOnClickListener {
            prevTango?.apply {
                TangoOperator.cancelOperate()
                tango = null
                loadNewTango(this)
                countView.text = "今日复习：${TangoOperator.review}\n" +
                        "今日已记：${TangoOperator.study}\n" +
                        "今日完成任务：${DataManager.todayMission}"
            }
        }
        // 长按单语，找到列表中对应的项
        layoutTango.setOnLongClickListener {
            tango?.apply { EventBus.getDefault().post(ChooseTangoEvent(this)) }
            true
        }
        // 长按单语，找到列表中对应的项
        writingView.setOnLongClickListener {
            tango?.apply { EventBus.getDefault().post(ChooseTangoEvent(this)) }
            true
        }
        // 按住记住按钮上滑一定高度，彻底记住单语
        rememberButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    // 若按住按钮上滑超过屏幕高度1/3，标识单语为彻底记住
                    val size = Point()
                    val wm = context.applicationContext
                            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    wm.defaultDisplay.getSize(size)
                    val h = size.y
                    val y = h - motionEvent.rawY
                    if (y > h / 3) {
                        operateTango(REMEMBER_FOREVER)
                    }
                }
            }
            false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun processLogic(view: View, savedInstanceState: Bundle?) {
        // 设置日文字体
        setJapaneseFont()
        // 显示学习记录
        countView.text = "今日复习：${TangoOperator.review}\n" +
                "今日已记：${TangoOperator.study}\n" +
                "今日完成任务：${DataManager.todayMission}"
        // 加载新单语
        loadNewTango()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * 检查单语信息是否全部显示，若全部显示则隐藏“显示答案”按钮
     */
    private fun checkAnswer() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            answerButton?.let {
                // 显示答案按钮渐隐
                answerLine.visibility = View.GONE
                answerButton.isEnabled = false
                val animator = ObjectAnimator.ofFloat(answerButton, "alpha", 1f, 0f) // 渐出效果
                animator.duration = 300
                animator.start()
            }
        }
    }

    /**
     * 渐入显示文本
     * @param tv 要显示的TextView
     */
    private fun showTextView(tv: TextView?) {
        tv?.apply {
            tv.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f) // 渐入效果
            animator.duration = 300
            animator.start()
        }
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
            // 若单语信息已全部显示则直接加载新单语
            loadNewTango()
        } else {
            // 若单语信息未全部显示则显示全部信息，并延迟一定时间后加载新单语
            showAnswer()
            object : Timer() {
                override fun timer() {
                    loadNewTango()
                }
            }.start(TangoConstants.NEW_TANGO_DELAY, true)
        }
    }

    /**
     * 测量文本宽度
     * @param textView 待测量的文本
     * @return 文本宽度
     */
    private fun measureWidth(textView: TextView): Float =
            textView.paint.measureText(textView.text.toString())

    /**
     * 加载新单语
     */
    private fun loadNewTango() {
        // 判断今日学习数是否超过目标
        val goal = DataManager.tangoGoal
        val reviewFlag = TangoOperator.study >= goal
        // 随机选取新单语
        val temp = TangoManager.randomTango(reviewFlag,
                DataManager.reviewFrequency, tango, false)
        // 加载新单语
        if (temp != null) {
            loadNewTango(temp)
        }
    }

    /**
     * 加载新单语
     * @param newTango 要加载的新单语
     */
    @SuppressLint("SetTextI18n")
    private fun loadNewTango(newTango: Tango) {
        // 初始化状态
        pronunciationFlag = false
        writingFlag = false
        meaningFlag = false
        // 渐入显示“显示答案”按钮
        answerLine.visibility = View.VISIBLE
        answerButton.isEnabled = true
        val animator = ObjectAnimator.ofFloat(answerButton, "alpha", 0f, 1f) // 渐入效果
        animator.duration = 300
        animator.start()
        // 防误触，一定时间后可以操作
        object : Timer() {
            override fun timer() {
                operateFlag = true
                rememberButton.setBackgroundColor(Color.TRANSPARENT)
                forgetButton.setBackgroundColor(Color.TRANSPARENT)
            }
        }.start(TangoConstants.INTERVAL_TIME_MIN, true)
        // 记录上一个单语
        if (tango != null) {
            prevTango = tango
            showPrevTango()
        } else {
            prevView.text = ""
        }
        // 设置新单语
        tango = newTango

        // 获取屏幕宽度使字体大小自动适应
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        writingView.textSize = textSize.toFloat()
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

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        countView.text = "今日复习：${TangoOperator.review}\n" +
                "今日已记：${TangoOperator.study}\n" +
                "今日完成任务：${DataManager.todayMission}"
    }

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        // 获取保存的字体设置
        val title = DataManager.japaneseFontTitle
        val font = TangoConstants.JAPANESE_FONT_MAP[title]
        // 获取设置的字体
        val mgr = context.assets
        var tf = Typeface.DEFAULT
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font)
        }
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
