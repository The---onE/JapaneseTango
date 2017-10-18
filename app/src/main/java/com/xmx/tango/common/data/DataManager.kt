package com.xmx.tango.common.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

import com.xmx.tango.module.tango.TangoConstants

// TODO
@SuppressLint("StaticFieldLeak")
/**
 * Created by The_onE on 2016/2/21.
 * 手机默认存储数据管理器
 */
object DataManager {

    private lateinit var mContext: Context
    private lateinit var mData: SharedPreferences

    /**
     * 初始化管理器
     */
    fun setContext(context: Context) {
        mContext = context
        mData = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
    }

    // 上次进行遗忘减分的时间
    var forgetLastTime: Long
        get() = getLong("last_forget_time", 0)
        set(time) = setLong("last_forget_time", time)

    // 上次运行时间
    var resetLastTime: Long
        get() = getLong("last_reset_time", 0)
        set(time) = setLong("last_reset_time", time)

    // 今日学习数
    var tangoStudy: Int
        get() = getInt("tango_study", 0)
        set(study) = setInt("tango_study", study)

    // 今日复习数
    var tangoReview: Int
        get() = getInt("tango_review", 0)
        set(review) = setInt("tango_review", review)

    // 今日完成任务数
    var todayMission: Int
        get() = getInt("today_mission", 0)
        set(mission) = setInt("today_mission", mission)

    // 每日学习目标
    var tangoGoal: Int
        get() = getInt("tango_goal", TangoConstants.DEFAULT_GOAL)
        set(goal) = setInt("tango_goal", goal)

    // 最大复习频率
    var reviewFrequency: Int
        get() = getInt("review_frequency", TangoConstants.REVIEW_FREQUENCY)
        set(frequency) = setInt("review_frequency", frequency)

    // 发音延迟时间
    var pronunciationTime: Float
        get() = getFloat("pronunciation_time", 2.5f)
        set(writingTime) = setFloat("pronunciation_time", writingTime)

    // 写法延迟时间
    var writingTime: Float
        get() = getFloat("writing_time", 3.0f)
        set(writingTime) = setFloat("writing_time", writingTime)

    // 解释延迟时间
    var meaningTime: Float
        get() = getFloat("meaning_time", 3.5f)
        set(meaningTime) = setFloat("meaning_time", meaningTime)

    // 学习/复习单语类型
    var tangoType: String
        get() = getString("tango_type", "")
        set(type) = setString("tango_type", type)

    // 学习/复习单语词性
    var partOfSpeech: String
        get() = getString("tango_part_of_speech", "")
        set(part) = setString("tango_part_of_speech", part)

    // 发音音色
    var tangoSpeaker: String
        get() = getString("tango_speaker", TangoConstants.SPEAKERS[0])
        set(speaker) = setString("tango_speaker", speaker)

    // 任务模式单语数
    var missionCount: Int
        get() = getInt("mission_count", TangoConstants.MISSION_COUNT)
        set(count) = setInt("mission_count", count)

    // 日文字体
    var japaneseFontTitle: String
        get() = getString("japanese_font_title", "默认")
        set(fontTitle) = setString("japanese_font_title", fontTitle)

    // 震动状态
    var vibratorStatus: Boolean
        get() = getBoolean("vibrator", true)
        set(flag) = setBoolean("vibrator", flag)

    // 服务切换间隔
    var serviceInterval: Int
        get() = getInt("service_interval", 5000)
        set(count) = setInt("service_interval", count)

    private fun getInt(key: String, def: Int): Int = mData.getInt(key, def)

    private fun setInt(key: String, value: Int) {
        val editor = mData.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun getFloat(key: String, def: Float): Float = mData.getFloat(key, def)

    private fun setFloat(key: String, value: Float) {
        val editor = mData.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    private fun getLong(key: String, def: Long): Long = mData.getLong(key, def)

    private fun setLong(key: String, value: Long) {
        val editor = mData.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    private fun getBoolean(key: String, def: Boolean): Boolean = mData.getBoolean(key, def)

    private fun setBoolean(key: String, value: Boolean) {
        val editor = mData.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun getString(key: String): String = mData.getString(key, "")

    private fun getString(key: String, def: String): String = mData.getString(key, def)

    private fun setString(key: String, value: String) {
        val editor = mData.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getSearchValue(key: String): String = getString("s_" + key)

    fun setSearchValue(key: String, value: String) {
        setString("s_" + key, value)
    }
}
