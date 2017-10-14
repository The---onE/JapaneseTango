package com.xmx.tango.module.calendar

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity

import cn.aigestudio.datepicker.bizs.calendars.DPCManager
import cn.aigestudio.datepicker.bizs.decors.DPDecor
import cn.aigestudio.datepicker.cons.DPMode
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*

/**
 * Created by The_onE on 2017/6/25.
 * 打卡签到Activity
 */
class CalendarActivity : BaseTempActivity() {

    internal var map: MutableMap<String, DateData> = HashMap() // 日期与数据的映射

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_calendar)
        // 初始化日历
        val c = Calendar.getInstance()
        datePicker.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1)
        datePicker.setTodayDisplay(true)
        datePicker.setMode(DPMode.NONE)

        val checkIn = ArrayList<String>() // 打卡的日期列表（显示背景）
        val info = ArrayList<String>() // 除今天外打卡的日期列表（显示数据信息）
        // 从数据库中获取数据
        val data = DateDataEntityManager.selectAll()
        data?.apply {
            for (item in this) {
                // 生成日历读取的字符串
                val key = item.year.toString() + "-" + item.month + "-" + item.date
                checkIn.add(key)
                // 日期与数据的映射
                map.put(key, item)
                // 不是今天则显示数据信息
                if (item.date != c.get(Calendar.DATE)
                        || item.month != c.get(Calendar.MONTH) + 1
                        || item.year != c.get(Calendar.YEAR)) {
                    info.add(key)
                }
            }
        }
        // 设置日历显示信息
        DPCManager.getInstance().setDecorBG(checkIn)
        DPCManager.getInstance().setDecorL(info)
        DPCManager.getInstance().setDecorT(info)
        DPCManager.getInstance().setDecorR(info)
        // 设置日历信息的显示方式
        datePicker.setDPDecor(object : DPDecor() {
            override fun drawDecorBG(canvas: Canvas, rect: Rect, paint: Paint, data: String?) {
                super.drawDecorBG(canvas, rect, paint, data)
                // 为打卡的日期设置背景颜色
                if (map.containsKey(data)) {
                    paint.color = Color.GREEN
                    canvas.drawCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), rect.width() / 2f, paint)
                }
            }

            override fun drawDecorL(canvas: Canvas, rect: Rect, paint: Paint, data: String?) {
                super.drawDecorL(canvas, rect, paint, data)
                // 为除今天外打卡的日期设置显示当天学习数
                if (map.containsKey(data)) {
                    map[data]?.apply {
                        paint.textSize = 20f
                        canvas.drawText("学" + this.study,
                                rect.centerX().toFloat(), rect.centerY().toFloat(), paint)
                    }
                }
            }

            override fun drawDecorT(canvas: Canvas, rect: Rect, paint: Paint, data: String?) {
                super.drawDecorT(canvas, rect, paint, data)
                // 为除今天外打卡的日期设置显示当天完成任务数
                if (map.containsKey(data)) {
                    map[data]?.apply {
                        paint.textSize = 20f
                        canvas.drawText("任" + this.mission,
                                rect.centerX().toFloat(), rect.centerY().toFloat(), paint)
                    }
                }
            }

            override fun drawDecorR(canvas: Canvas, rect: Rect, paint: Paint, data: String?) {
                super.drawDecorR(canvas, rect, paint, data)
                // 为除今天外打卡的日期设置显示当天复习数
                if (map.containsKey(data)) {
                    map[data]?.apply {
                        paint.textSize = 20f
                        canvas.drawText("复" + this.review, rect.centerX().toFloat(), rect.centerY().toFloat(), paint)
                    }
                }
            }
        })
    }

    override fun setListener() {

    }

    override fun processLogic(savedInstanceState: Bundle?) {

    }
}
