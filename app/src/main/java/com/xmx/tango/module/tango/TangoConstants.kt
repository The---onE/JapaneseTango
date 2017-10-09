package com.xmx.tango.module.tango

import java.util.LinkedHashMap

/**
 * Created by The_onE on 2017/5/16.
 * 单语相关常量
 */
object TangoConstants {
    // 可选朗读音色
    val SPEAKERS = arrayOf("male01", "female01", "male02")
    // 音调显示字符
    val TONES = arrayOf("◎", "①", "②", "③", "④", "⑤", "⑥", "⑦")
    // 字体大小
    val DEFAULT_PRONUNCIATION_TEXT_SIZE = 36
    val DEFAULT_WRITING_TEXT_SIZE = 42
    val DEFAULT_MEANING_TEXT_SIZE = 36
    val DEFAULT_TEST_WRITING_TEXT_SIZE = 36
    val DEFAULT_TEST_MEANING_TEXT_SIZE = 42
    // 分数相关常量
    val REMEMBER_SCORE = 7
    val REMEMBER_MIN_SCORE = 4
    val FORGET_SCORE = -3
    val REMEMBER_FOREVER_SCORE = 64
    // 当日学习或复习疲劳系数
    val TIRED_COEFFICIENT = 35
    // 复习相关常量
    val REVIEW_FREQUENCY = 5
    val TODAY_CONSECUTIVE_REVIEW_MAX = 10
    // 任务模式单语个数
    val MISSION_COUNT = 20
    // 防止误操作时间间隔
    val INTERVAL_TIME_MIN: Long = 500
    // 显示新信息延迟时间
    val NEW_TANGO_DELAY: Long = 1000
    val SHOW_ANSWER_DELAY: Long = 2000
    // 默认每日目标
    val DEFAULT_GOAL = 30
    // 默认待选单语列表数量
    val DEFAULT_LIMIT = 8
    // 震动时间
    val KEYBOARD_INPUT_VIBRATE_TIME: Long = 50
    val TEST_RIGHT_VIBRATE_TIME: Long = 200
    val REMEMBER_VIBRATE_TIME: Long = 100
    val REMEMBER_FOREVER_VIBRATE_TIME: Long = 200
    val FORGET_VIBRATE_TIME: Long = 100
    // 识别动词分类关键字
    val VERB_FLAG = "动"
    val VERB1_FLAG = "动1"
    val VERB2_FLAG = "动2"
    val VERB3_FLAG = "动3"
    // 字体名称与文件名映射
    val JAPANESE_FONT_MAP: MutableMap<String, String?> = LinkedHashMap()

    init {
        JAPANESE_FONT_MAP.put("默认", null)
        JAPANESE_FONT_MAP.put("A-OTF 毎日新聞", "A-OTF-MNewsMPro-Light.otf")
        JAPANESE_FONT_MAP.put("A-OTF くもやじ", "A-OTF-KumoyaStd-Regular.otf")
        JAPANESE_FONT_MAP.put("京円", "京円.ttf")
    }

    // 每日遗忘函数
    fun forgottenScore(source: Int): Int = source * 4 / 5
}
