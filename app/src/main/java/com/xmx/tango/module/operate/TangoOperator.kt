package com.xmx.tango.module.operate

import com.xmx.tango.core.Constants
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.calendar.DateData
import com.xmx.tango.module.calendar.DateDataEntityManager
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoEntityManager

import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by The_onE on 2016/10/7./
 * 单语操作管理器
 */
object TangoOperator {
    var study: Int = 0 // 今日学习数
    var review: Int = 0 // 今日复习数
    private var todayConsecutive = 0 // 同一复习频率单语连续复习数

    // 保存上一次时操作的信息
    private var prevTango: Tango? = null
    private var prevStudy: Int = 0
    private var prevReview: Int = 0

    init {
        study = DataManager.getInstance().tangoStudy
        review = DataManager.getInstance().tangoReview
        // 上次运行时间
        val last = Date(DataManager.getInstance().resetLastTime)
        val lastCalendar = Calendar.getInstance()
        lastCalendar.time = last
        // 当前时间
        val now = Date()
        val nowCalendar = Calendar.getInstance()
        nowCalendar.time = now
        if (!Constants.isSameDate(now, last)) {
            if (last.time > 0) {
                // 更新上次签到日数据
                var dateData = DateDataEntityManager
                        .selectLatest("addTime", false,
                                "Year=" + (lastCalendar.get(Calendar.YEAR)),
                                "Month=" + (lastCalendar.get(Calendar.MONTH) + 1),
                                "Date=" + lastCalendar.get(Calendar.DATE))
                if (dateData != null) {
                    // 更新上次签到学习、复习数
                    DateDataEntityManager.updateData(dateData.id,
                            "Study=" + study,
                            "Review=" + review)
                } else {
                    // 创建签到记录
                    dateData = DateData()
                    dateData.year = lastCalendar.get(Calendar.YEAR)
                    dateData.month = lastCalendar.get(Calendar.MONTH) + 1
                    dateData.date = lastCalendar.get(Calendar.DATE)
                    dateData.checkIn = 1
                    dateData.study = study
                    dateData.review = review
                    dateData.addTime = now
                    DateDataEntityManager.insertData(dateData)
                }
            }
            // 今天打卡签到
            var todayData = DateDataEntityManager
                    .selectLatest("addTime", false,
                            "Year=" + (nowCalendar.get(Calendar.YEAR)),
                            "Month=" + (nowCalendar.get(Calendar.MONTH) + 1),
                            "Date=" + nowCalendar.get(Calendar.DATE))
            if (todayData == null) {
                todayData = DateData()
                todayData.year = nowCalendar.get(Calendar.YEAR)
                todayData.month = nowCalendar.get(Calendar.MONTH) + 1
                todayData.date = nowCalendar.get(Calendar.DATE)
                todayData.checkIn = 1
                todayData.addTime = now
                DateDataEntityManager.insertData(todayData)
            }
            // 清空上次数据
            study = 0
            DataManager.getInstance().tangoStudy = 0
            review = 0
            DataManager.getInstance().tangoReview = 0
            DataManager.getInstance().reviewFrequency = TangoConstants.REVIEW_FREQUENCY
            DataManager.getInstance().resetLastTime = now.time
        }
    }

    /**
     * 记住了单语
     * @param tango 记住的单语
     */
    fun remember(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            var frequency = tango.frequency
            val goal = DataManager.getInstance().tangoGoal
            if (!Constants.isSameDate(now, last)) {
                // 今天第一次学习或复习该单语
                todayConsecutive = 0 // 重置同一复习频率单语连续复习数
                if (last != null && last.time > 0) {
                    // 复习
                    review++
                    DataManager.getInstance().tangoReview = review
                    // 每天第一次复习后降低复习频率
                    if (frequency > 0) {
                        frequency--
                    }
                } else {
                    // 学习
                    study++
                    DataManager.getInstance().tangoStudy = study
                    frequency = TangoConstants.REVIEW_FREQUENCY
                }
            } else if (study >= goal) {
                // 学习数已达到目标（在复习阶段）
                todayConsecutive++
                // 同一复习频率单语连续复习数过多
                if (todayConsecutive > TangoConstants.TODAY_CONSECUTIVE_REVIEW_MAX) {
                    todayConsecutive = 0
                    var frequencyMax = DataManager.getInstance().reviewFrequency
                    frequencyMax--
                    DataManager.getInstance().reviewFrequency = frequencyMax
                }
            }
            // 处理记住了操作的分数
            var score = TangoConstants.REMEMBER_SCORE - (study + review) / TangoConstants.TIRED_COEFFICIENT
            score = Math.max(score, TangoConstants.REMEMBER_MIN_SCORE)
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + score),
                    "Frequency=" + frequency,
                    "LastTime=" + Date().time)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 没记住操作
     * @param tango 没记住的单语
     */
    fun forget(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            var frequency = tango.frequency
            if (!Constants.isSameDate(now, last)) {
                if (last != null && last.time > 0) {
                    // 遗忘了待复习的单语则提高复习频率
                    frequency++
                    if (frequency > TangoConstants.REVIEW_FREQUENCY) {
                        frequency = TangoConstants.REVIEW_FREQUENCY
                    }
                }
            }
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.FORGET_SCORE),
                    "Frequency=" + frequency)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 彻底记住单语
     * @param tango 彻底记住的单语
     */
    fun rememberForever(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            if (!Constants.isSameDate(now, last)) {
                if (last!!.time > 0) {
                    // 复习
                    review++
                    DataManager.getInstance().tangoReview = review
                } else {
                    // 学习
                    study++
                    DataManager.getInstance().tangoStudy = study
                }
            }
            // 复习频率为-1不会再被复习到
            val frequency = -1
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_FOREVER_SCORE),
                    "Frequency=" + frequency,
                    "LastTime=" + Date().time)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 未经提示拼写正确
     * @param tango 未经提示拼写正确的单语
     */
    fun rightWithoutHint(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            var frequency = tango.frequency
            if (!Constants.isSameDate(now, last)) {
                // 今天第一次复习该单语
                if (last != null && last.time > 0) {
                    // 复习
                    review++
                    DataManager.getInstance().tangoReview = review
                }
                if (frequency > 0) {
                    // 降低复习频率
                    frequency -= 2
                    if (frequency < 0) {
                        frequency = 0
                    }
                }
            } else {
                todayConsecutive++
                // 同一复习频率单语连续复习数过多
                if (todayConsecutive > TangoConstants.TODAY_CONSECUTIVE_REVIEW_MAX) {
                    todayConsecutive = 0
                    var frequencyMax = DataManager.getInstance().reviewFrequency
                    frequencyMax--
                    DataManager.getInstance().reviewFrequency = frequencyMax
                }
            }
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_SCORE * 2),
                    "Frequency=" + frequency,
                    "LastTime=" + Date().time)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 经提示拼写正确
     * @param tango 经提示拼写正确的单语
     */
    fun rightWithHint(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            var frequency = tango.frequency
            if (!Constants.isSameDate(now, last)) {
                // 今天第一次复习该单语
                if (last != null && last.time > 0) {
                    // 复习
                    review++
                    DataManager.getInstance().tangoReview = review
                }
                // 降低复习频率
                if (frequency > 0) {
                    frequency--
                }
            }
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_SCORE),
                    "Frequency=" + frequency,
                    "LastTime=" + Date().time)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 未拼写成功，点击查看答案
     */
    fun wrong(tango: Tango?) {
        if (tango != null && tango.id > 0) {
            // 保存操作前的状态
            prevTango = tango
            prevStudy = study
            prevReview = review

            val last = tango.lastTime
            val now = Date()
            var frequency = tango.frequency
            if (!Constants.isSameDate(now, last)) {
                // 今天第一次复习该单语
                if (last != null && last.time > 0) {
                    // 增加复习频率
                    frequency++
                    if (frequency > TangoConstants.REVIEW_FREQUENCY) {
                        frequency = TangoConstants.REVIEW_FREQUENCY
                    }
                }
            }
            // 更新数据库中单语学习信息
            TangoEntityManager.updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.FORGET_SCORE / 2),
                    "Frequency=" + frequency)
            EventBus.getDefault().post(OperateTangoEvent())
        }
    }

    /**
     * 撤销上次操作
     */
    fun cancelOperate() {
        prevTango?.apply {
            if (this.id > 0) {
                // 恢复上次保存的状态
                TangoEntityManager.updateData(this.id,
                        "Score=" + this.score,
                        "Frequency=" + this.frequency,
                        "LastTime=" + this.lastTime!!.time)
                study = prevStudy
                review = prevReview
            }
        }
    }
}
