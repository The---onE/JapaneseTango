package com.xmx.tango.module.test

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.xmx.tango.R

import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.sentence.SentenceActivity
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.utils.StrUtil
import com.xmx.tango.utils.VibratorUtil
import kotlinx.android.synthetic.main.activity_typewriter.*

/**
 * Created by The_onE on 2017/9/29.
 * 写字板Activity
 */
class TypewriterActivity : BaseTempActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_typewriter)

        // 初始化假名键盘
        keyboardView.init { result -> inputToEdit(result) }
        keyboardView.enable()
        // 设置日文字体
        setJapaneseFont()
    }

    override fun setListener() {
        // 输入框点击不弹出输入法
        typewriterEdit.onFocusChangeListener = View.OnFocusChangeListener { view, _ -> hideSoftInput(view) }
        typewriterEdit.setOnClickListener { view -> hideSoftInput(view) }
        // 退格
        btnBackspace.setOnClickListener {
            val s = typewriterEdit.text
            // 删除光标前的字符
            val index = typewriterEdit.selectionStart
            if (index > 0) {
                s.delete(index - 1, index)
            }
            // 震动提示
            if (DataManager.vibratorStatus) {
                VibratorUtil.vibrate(this@TypewriterActivity,
                        TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
            }
        }
        // 长按退格，清空文本框
        btnBackspace.setOnLongClickListener {
            typewriterEdit.text.clear()
            true
        }
        // 添加空格
        btnSpace.setOnClickListener { inputToEdit(" ") }
        // 输入ます
        btnMasu.setOnClickListener { inputToEdit("ます") }
        // 弹出输入法键盘
        btnKeyboard.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(typewriterEdit, 0)
        }
        // 将输入的字符复制到剪贴板
        btnCopy.setOnClickListener {
            val text = typewriterEdit.text.toString()
            if (text.isNotBlank()) {
                StrUtil.copyToClipboard(this@TypewriterActivity, text)
                // 震动提示
                if (DataManager.vibratorStatus) {
                    VibratorUtil.vibrate(this@TypewriterActivity,
                            TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
                }
                showToast("已复制到剪切板")
            } else {
                showToast("请输入内容")
            }
        }
        // 分词
        btnKuromoji.setOnClickListener {
            val sentence = typewriterEdit.text.toString()
            if (sentence.isNotBlank()) {
                // 打开分词Activity
                val intent = Intent(this@TypewriterActivity, SentenceActivity::class.java)
                intent.putExtra("sentence", sentence)
                startActivity(intent)
            } else {
                showToast("请输入内容")
            }
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {}

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        val tf = DataManager.getJapaneseTypeface()
        typewriterEdit.typeface = tf
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
     * 在编辑框中插入文字
     * @param re 要插入的字符串
     */
    private fun inputToEdit(re: String) {
        val i = typewriterEdit.selectionStart
        val s = typewriterEdit.text
        s.insert(i, re) // 在光标处插入文字
        // 震动提示
        if (DataManager.vibratorStatus) {
            VibratorUtil.vibrate(this@TypewriterActivity,
                    TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
        }
    }
}
