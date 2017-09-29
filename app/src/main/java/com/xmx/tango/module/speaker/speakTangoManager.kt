package com.xmx.tango.module.speaker

import android.content.Context
import android.speech.tts.TextToSpeech

import com.xmx.tango.common.data.DataManager

import java.util.HashMap

/**
 * Created by The_onE on 2016/9/20.
 * 日语TTS管理器
 */
object speakTangoManager {

    /**
     * 朗读字符串
     * @param context 当前上下文
     * @param tango 要朗读的字符串
     */
    fun speak(context: Context, tango: String) {
        val tts = JapaneseTextToSpeech(context, null)
        val params = HashMap<String, String>()
        val speaker = DataManager.getInstance().tangoSpeaker // 获取设置的音色
        params.put(JapaneseTextToSpeech.KEY_PARAM_SPEAKER, speaker)
        tts.speak(tango, TextToSpeech.QUEUE_FLUSH, params)
    }
}
