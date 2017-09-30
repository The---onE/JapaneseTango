package com.xmx.tango.module.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.xmx.tango.utils.StrUtil

/**
 * Created by The_onE on 2017/5/30.
 * 假名浮动键盘
 */
class FlickView(context: Context) : View(context) {

    private var blockPaint: Paint = Paint() // 背景画笔
    private var textPaint: Paint = Paint() // 假名画笔
    private var deltaY: Float = 0.toFloat() // 纠正通知栏产生的高度误差

    private var px: Float = 0.toFloat() // 中心位置横坐标
    private var py: Float = 0.toFloat() // 中心位置纵坐标
    private var kanaArray: Array<String> = arrayOf() // 用于选择的假名

    companion object {
        val BLOCK_WIDTH = 200f // 背景格宽度
        val BLOCK_HEIGHT = 200f // 背景格高度
        val HALF_WIDTH = BLOCK_WIDTH / 2 // 背景半格宽度
        val HALF_HEIGHT = BLOCK_HEIGHT / 2 // 背景半格高度

        private val TEXT_SIZE = 80f // 文字大小
    }

    init {
        px = -BLOCK_WIDTH * 2
        py = -BLOCK_HEIGHT * 2
        // 初始化背景画笔
        blockPaint.color = Color.LTGRAY
        blockPaint.isAntiAlias = true
        blockPaint.style = Paint.Style.FILL_AND_STROKE
        // 初始化假名画笔
        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.CENTER
        // 纠正通知栏产生的高度误差
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.bottom - fontMetrics.top
        deltaY = fontHeight / 2 - fontMetrics.bottom
    }

    /**
     * 显示浮动键盘
     * @param cx 中心点横坐标
     * @param cy 中心点纵坐标
     * @param kana 可选的五个假名
     */
    fun show(cx: Float, cy: Float, kana: Array<String>) {
        px = cx
        py = cy
        kanaArray = kana
        postInvalidate()
    }

    /**
     * 隐藏浮动键盘
     */
    fun remove() {
        px = -BLOCK_WIDTH * 2
        py = -BLOCK_HEIGHT * 2
        kanaArray = arrayOf()
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (kanaArray.size == 5) {
            // 绘制浮动键盘背景
            canvas.drawRect(px - HALF_WIDTH, py - HALF_HEIGHT, px + HALF_WIDTH, py + HALF_HEIGHT, blockPaint)
            canvas.drawRect(px - HALF_WIDTH, py - HALF_HEIGHT * 3, px + HALF_WIDTH, py - HALF_HEIGHT, blockPaint)
            canvas.drawRect(px - HALF_WIDTH, py + HALF_HEIGHT, px + HALF_WIDTH, py + HALF_HEIGHT * 3, blockPaint)
            canvas.drawRect(px - HALF_WIDTH * 3, py - HALF_HEIGHT, px - HALF_WIDTH, py + HALF_HEIGHT, blockPaint)
            canvas.drawRect(px + HALF_WIDTH, py - HALF_HEIGHT, px + HALF_WIDTH * 3, py + HALF_HEIGHT, blockPaint)
            // 绘制浮动键盘假名
            canvas.drawText(kanaArray[0], px, py + deltaY, textPaint)
            canvas.drawText(kanaArray[1], px - BLOCK_WIDTH, py + deltaY, textPaint)
            canvas.drawText(kanaArray[2], px, py - BLOCK_HEIGHT + deltaY, textPaint)
            canvas.drawText(kanaArray[3], px + BLOCK_WIDTH, py + deltaY, textPaint)
            canvas.drawText(kanaArray[4], px, py + BLOCK_HEIGHT + deltaY, textPaint)
            for (i in kanaArray) {
                StrUtil.showLog(i, i)
            }
        }
    }
}
