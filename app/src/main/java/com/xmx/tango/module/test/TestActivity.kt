package com.xmx.tango.module.test

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.xmx.tango.R

import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.module.operate.TangoOperator
import com.xmx.tango.utils.Timer
import com.xmx.tango.utils.VibratorUtil
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by The_onE on 2017/9/29.
 * 测试模式Activity
 */
class TestActivity : BaseTempActivity() {

    private var tango: Tango? = null
    private var prevTango: Tango? = null
    private var hintFlag = false
    private var writingFlag = false
    private var enableFlag = false

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_test)

        // 初始化假名键盘
        keyboardView.init { result -> inputToEdit(result) }
        // 设置日语字体
        setJapaneseFont()
    }

    override fun setListener() {
        // 输入框点击不弹出输入法
        testEdit.onFocusChangeListener = View.OnFocusChangeListener { view, _ -> hideSoftInput(view) }
        testEdit.setOnClickListener { view -> hideSoftInput(view) }
        // 显示答案
        btnAnswer.setOnClickListener {
            if (enableFlag) {
                showAnswer()
            }
        }
        // 显示提示
        btnHint.setOnClickListener {
            if (enableFlag) {
                showWriting()
            }
        }
        // 退格
        btnBackspace.setOnClickListener {
            if (enableFlag) {
                val s = testEdit.text
                // 删除光标前的字符
                val index = testEdit.selectionStart
                if (index > 0) {
                    s.delete(index - 1, index)
                }
                // 震动提示
                if (DataManager.getInstance().vibratorStatus) {
                    VibratorUtil.vibrate(this@TestActivity,
                            TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
                }
                // 检查答案
                checkAnswer()
            }
        }
        // 长按退格，清空文本框
        btnBackspace.setOnLongClickListener {
            if (enableFlag) {
                testEdit.text.clear()
            }
            true
        }
        // 添加空格
        btnSpace.setOnClickListener {
            if (enableFlag) {
                inputToEdit(" ")
            }
        }
        // 输入ます
        btnMasu.setOnClickListener {
            if (enableFlag) {
                inputToEdit("ます")
            }
        }
        // 输入斜线
        btnSlash.setOnClickListener {
            if (enableFlag) {
                inputToEdit("/")
            }
        }
        // 弹出输入法键盘
        btnKeyboard.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(testEdit, 0)
        }
    }

    /**
     * 渐变显示文本
     * @param tv 文本框
     */
    private fun showTextView(tv: TextView) {
        tv.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f) // 渐入效果
        animator.duration = 300
        animator.start()
    }

    override fun processLogic(savedInstanceState: Bundle?) {
        loadNewTango() // 加载新单语
    }

    /**
     * 加载新单语
     */
    @SuppressLint("SetTextI18n")
    private fun loadNewTango() {
        // 获取文本宽度
        val wm = applicationContext
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getSize(p)
        val width = p.x
        var length: Float // 文字宽度

        prevTango = tango // 保存当前单语
        // 随机获取下一个单语
        tango = TangoManager.randomTango(true,
                DataManager.getInstance().reviewFrequency, prevTango, false)
        // 测试单语的解释不能为空
        var count = 0 // 避免死循环计数
        while (tango == null || tango?.meaning.isNullOrBlank()) {
            // 重新获取新单语
            tango = TangoManager.randomTango(true,
                    DataManager.getInstance().reviewFrequency, prevTango, false)
            count++
            if (count > 100) {
                showToast("没有符合条件的测试")
                finish()
            }
        }

        // 设置解释
        var textSize = TangoConstants.DEFAULT_TEST_MEANING_TEXT_SIZE// 文字大小
        if (tango?.partOfSpeech?.isNotBlank() == true) {
            // 设置词性
            meaningView.text = "[${tango?.partOfSpeech}]${tango?.meaning}"
        } else {
            meaningView.text = tango?.meaning
        }
        meaningView.textSize = textSize.toFloat()
        meaningView.visibility = View.INVISIBLE
        // 逐渐尝试缩小字体直到可以在一行中显示
        length = measureWidth(meaningView)
        while (length > width) {
            textSize -= 1
            meaningView.textSize = textSize.toFloat()
            length = measureWidth(meaningView)
        }

        // 设置写法
        textSize = TangoConstants.DEFAULT_TEST_WRITING_TEXT_SIZE
        writingView.text = tango?.writing
        writingView.textSize = textSize.toFloat()
        writingView.visibility = View.INVISIBLE
        // 逐渐尝试缩小字体直到可以在一行中显示
        length = measureWidth(writingView)
        while (length > width) {
            textSize -= 1
            writingView.textSize = textSize.toFloat()
            length = measureWidth(writingView)
        }

        // 初始化一次测试
        showTextView(meaningView)
        hintFlag = false
        writingFlag = false
        testEdit.setText("")
        testEdit.isEnabled = true
        enableFlag = true
        keyboardView.enable()
    }

    /**
     * 显示答案
     */
    private fun showAnswer() {
        TangoOperator.wrong(tango) // 显示答案视为没记住
        testEdit.setText(tango?.pronunciation) // 显示正确发音
        if (!writingFlag) {
            showTextView(writingView) // 显示写法
        }
        // 暂时不能操作
        testEdit.isEnabled = false
        enableFlag = false
        keyboardView.disable()
        // 一定时间后加载新单语
        object : Timer() {
            override fun timer() {
                loadNewTango()
            }
        }.start(TangoConstants.SHOW_ANSWER_DELAY, true)
    }

    /**
     * 显示写法
     */
    private fun showWriting() {
        if (!writingFlag) {
            showTextView(writingView)
            writingFlag = true
        }
        hintFlag = true
    }

    /**
     * 检查输入答案是否正确
     */
    private fun checkAnswer() {
        if (testEdit.text.toString() == tango?.pronunciation) {
            if (!hintFlag) {
                TangoOperator.rightWithoutHint(tango) // 未提示答对
            } else {
                TangoOperator.rightWithHint(tango) // 经提示答对
            }
            if (!writingFlag) {
                showTextView(writingView) // 显示写法
            }
            // 暂时不能操作
            testEdit.isEnabled = false
            enableFlag = false
            keyboardView.disable()
            // 震动提示
            if (DataManager.getInstance().vibratorStatus) {
                VibratorUtil.vibrate(this@TestActivity,
                        TangoConstants.TEST_RIGHT_VIBRATE_TIME)
            }
            // 一定时间后加载新单语
            object : Timer() {
                override fun timer() {
                    loadNewTango()
                }
            }.start(TangoConstants.NEW_TANGO_DELAY, true)
        }
    }

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        // 获取保存的字体设置
        val title = DataManager.getInstance().japaneseFontTitle
        var font: String? = null
        if (title != null) {
            font = TangoConstants.JAPANESE_FONT_MAP[title]
        }
        // 获取设置的字体
        var tf = Typeface.DEFAULT
        val mgr = assets
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font)
        }
        // 为日文设置字体
        testEdit.typeface = tf
        writingView.typeface = tf
    }

    /**
     * 隐藏输入法
     * @param view 焦点View
     */
    private fun hideSoftInput(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 测量文本框宽度
     * @param textView 文本框
     * @return 文本框宽度
     */
    private fun measureWidth(textView: TextView): Float =
            textView.paint.measureText(textView.text.toString())

    /**
     * 在编辑框中插入文字
     * @param re 要插入的字符串
     */
    private fun inputToEdit(re: String) {
        val i = testEdit.selectionStart
        val s = testEdit.text
        s.insert(i, re) // 在光标处插入文字
        // 震动提示
        if (DataManager.getInstance().vibratorStatus) {
            VibratorUtil.vibrate(this@TestActivity,
                    TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
        }
        checkAnswer()
    }
}
