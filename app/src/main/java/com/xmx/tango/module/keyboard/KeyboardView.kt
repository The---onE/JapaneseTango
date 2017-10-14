package com.xmx.tango.module.keyboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout

import com.xmx.tango.R
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.utils.StrUtil
import com.xmx.tango.utils.VibratorUtil

import java.util.HashMap

/**
 * Created by The_onE on 2017/6/7.
 * 假名键盘
 */
class KeyboardView(private val mContext: Context, attrs: AttributeSet) : GridLayout(mContext, attrs) {
    private val buttons = HashMap<Button, Int>() // 按钮与按钮假名行的映射
    private var flickView: FlickView? = null // 浮动键盘
    private var inputCallback: ((str: String) -> Unit)? = null

    private var voicing = 0 // 清音/浊音
    private var kata = 0 // 平假名/片假名

    private var startBase: Int = 0 // 点击的あ段假名在列表中的位置
    private var startX: Float = 0.toFloat() // 点击起点位置横坐标
    private var startY: Float = 0.toFloat() // 点击起点位置纵坐标

    private var enableFlag = false // 是否可用
    private var initFlag = false // 是否已初始化

    companion object {
        private val KANA_SIZE = 30 // 假名字体大小
        private val SWITCH_SIZE = 24 // 切换按钮
    }

    init {
        View.inflate(mContext, R.layout.view_keyboard, this)

        var rowSpec: GridLayout.Spec // 表格布局行属性
        var columnSpec: GridLayout.Spec // 表格布局列属性
        var layoutParams: GridLayout.LayoutParams // 表格布局属性

        for (i in 1..KeyboardConstants.VOICING_LINES) {
            // 每行假名一个按钮
            val button = object : AppCompatButton(mContext) {
                override fun performClick(): Boolean {
                    super.performClick()
                    return false
                }
            }
            // あ段假名
            val base = (i - 1) * 5
            button.text = KeyboardConstants.KANA_ARRAY[base]
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, KANA_SIZE.toFloat())
            button.setBackgroundColor(Color.TRANSPARENT)
            button.setOnTouchListener(OnTouchListener { view, motionEvent ->
                // 设置触摸事件
                if (!initFlag) {
                    // 必须先进行初始化
                    StrUtil.showToast(mContext, "必须先初始化键盘!")
                    return@OnTouchListener false
                }
                if (enableFlag) {
                    // 在可用状态下
                    val x = motionEvent.rawX // 触摸点横坐标
                    var statusHeight = 0

                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // 处理触摸点纵坐标
                            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                            if (resourceId > 0) {
                                statusHeight = resources.getDimensionPixelSize(resourceId)
                            }
                            val y = motionEvent.rawY - statusHeight // 纠正后的触摸点纵坐标

                            val line = buttons[view] // 获取点击按钮所在行
                            line?.apply {
                                var index = line
                                // 处理浊音、片假名切换
                                index += voicing * KeyboardConstants.VOICING_LINES
                                index += kata * KeyboardConstants.KATA_LINES
                                val lineBase = (index - 1) * 5
                                // 显示浮动键盘
                                flickView?.show(x, y, arrayOf(KeyboardConstants.KANA_ARRAY[lineBase], KeyboardConstants.KANA_ARRAY[lineBase + 1], KeyboardConstants.KANA_ARRAY[lineBase + 2], KeyboardConstants.KANA_ARRAY[lineBase + 3], KeyboardConstants.KANA_ARRAY[lineBase + 4]))
                                // 记录初始信息
                                startBase = lineBase
                                startX = x
                                startY = y
                            }
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                            // 处理触摸点纵坐标
                            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                            if (resourceId > 0) {
                                statusHeight = resources.getDimensionPixelSize(resourceId)
                            }
                            val y = motionEvent.rawY - statusHeight // 纠正后的触摸点纵坐标

                            // 隐藏浮动键盘
                            flickView?.remove()
                            // 根据位置获取输入的假名
                            var result: String? = null
                            if (x < startX - FlickView.HALF_WIDTH) {
                                if (startY - FlickView.HALF_HEIGHT < y && y < startY + FlickView.HALF_HEIGHT) {
                                    result = KeyboardConstants.KANA_ARRAY[startBase + 1]
                                }
                            } else if (x > startX + FlickView.HALF_WIDTH) {
                                if (startY - FlickView.HALF_HEIGHT < y && y < startY + FlickView.HALF_HEIGHT) {
                                    result = KeyboardConstants.KANA_ARRAY[startBase + 3]
                                }
                            } else {
                                result = when {
                                    y < startY - FlickView.HALF_HEIGHT -> KeyboardConstants.KANA_ARRAY[startBase + 2]
                                    y > startY + FlickView.HALF_HEIGHT -> KeyboardConstants.KANA_ARRAY[startBase + 4]
                                    else -> KeyboardConstants.KANA_ARRAY[startBase]
                                }
                            }
                            // 如果在合法位置则调用输入回调
                            result?.apply {
                                if (this.isNotBlank()) {
                                    inputCallback?.let { it(this) }
                                }
                            }
                        }
                    }
                    return@OnTouchListener true
                }
                false
            })
            // 计算所在行
            val row = (i - 1) / 3
            rowSpec = GridLayout.spec(row)
            // 计算所在列
            val column = if (i != KeyboardConstants.VOICING_LINES) {
                // 一般行按顺序排列
                (i - 1) % 3
            } else {
                // 末行在中间列
                1
            }
            // 应用表格布局并添加到布局中
            columnSpec = GridLayout.spec(column)
            layoutParams = GridLayout.LayoutParams(rowSpec, columnSpec)
            this.addView(button, layoutParams)
            // 添加按钮与按钮假名行映射
            buttons.put(button, i)
        }
        // 添加清音/浊音切换按钮
        val btnVoicing = Button(mContext)
        btnVoicing.text = "清/浊"
        btnVoicing.setTextSize(TypedValue.COMPLEX_UNIT_SP, SWITCH_SIZE.toFloat())
        btnVoicing.setBackgroundColor(Color.TRANSPARENT)
        btnVoicing.setOnClickListener {
            voicing = if (voicing == 0) 1 else 0
            // 更新所有按钮
            updateButton()
            // 震动提示
            if (DataManager.vibratorStatus) {
                VibratorUtil.vibrate(mContext, TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
            }
        }
        rowSpec = GridLayout.spec(3)
        columnSpec = GridLayout.spec(0)
        layoutParams = GridLayout.LayoutParams(rowSpec, columnSpec)
        this.addView(btnVoicing, layoutParams)
        // 添加平假名/片假名切换按钮
        val btnKata = Button(mContext)
        btnKata.text = "平/片"
        btnKata.setTextSize(TypedValue.COMPLEX_UNIT_SP, SWITCH_SIZE.toFloat())
        btnKata.setBackgroundColor(Color.TRANSPARENT)
        btnKata.setOnClickListener {
            kata = if (kata == 0) 1 else 0
            // 更新所有按钮
            updateButton()
            // 震动提示
            if (DataManager.vibratorStatus) {
                VibratorUtil.vibrate(mContext, TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
            }
        }
        rowSpec = GridLayout.spec(3)
        columnSpec = GridLayout.spec(2)
        layoutParams = GridLayout.LayoutParams(rowSpec, columnSpec)
        this.addView(btnKata, layoutParams)
    }

    /**
     * 初始化键盘
     * @param callback 输入回调
     */
    fun init(callback: ((str: String) -> Unit)) {
        // 向上查找父Content，直到找到所属的Activity
        var c = mContext
        var parent: Activity? = null
        while (c is ContextWrapper) {
            if (mContext is Activity) {
                parent = mContext
            }
            c = (mContext as ContextWrapper).baseContext
        }
        if (parent != null) {
            flickView = FlickView(parent)
            parent.addContentView(flickView,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT))
            initFlag = true
            inputCallback = callback
        } else {
            StrUtil.showToast(mContext, "找不到所属Activity，无法初始化!")
        }
    }

    /**
     * 更新所有按钮
     */
    private fun updateButton() {
        for (b in buttons.keys) {
            val index = buttons[b]
            index?.apply {
                var i = index
                // 更新按钮浊音、片假名状态
                i += voicing * KeyboardConstants.VOICING_LINES
                i += kata * KeyboardConstants.KATA_LINES
                val base = (i - 1) * 5
                b.text = KeyboardConstants.KANA_ARRAY[base]
            }
        }
    }

    /**
     * 启用键盘
     */
    fun enable() {
        enableFlag = true
    }

    /**
     * 禁用键盘
     */
    fun disable() {
        enableFlag = false
    }
}
