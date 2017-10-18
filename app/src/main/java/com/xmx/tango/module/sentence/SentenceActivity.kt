package com.xmx.tango.module.sentence

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.atilika.kuromoji.ipadic.Token
import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.utils.StrUtil
import com.xmx.tango.utils.VibratorUtil
import kotlinx.android.synthetic.main.activity_sentence.*

/**
 * Created by The_onE on 2017/9/29.
 * 句子分词Activity
 */
class SentenceActivity : BaseTempActivity() {

    private var tokenList: List<Token> = ArrayList() // 句子成分列表
    private var typeface: Typeface? = null // 日文字体
    private var task: AsyncTask<Void, Void, List<Token>>? = null // 分词任务

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_sentence)
        // 获取待分词句子
        val sentence = intent.getStringExtra("sentence")
        if (sentence.isNullOrBlank()) {
            finish()
        }
        sentenceView.text = sentence
        // 点击句子朗读
        sentenceView.setOnClickListener { SpeakTangoManager.speak(this@SentenceActivity, sentence) }

        // 开始分词任务
        task = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, List<Token>>() {
            override fun doInBackground(vararg voids: Void): List<Token> =
                    SentenceUtil.analyzeSentence(sentence) // 调用分词工具分词

            override fun onPostExecute(tokens: List<Token>) {
                super.onPostExecute(tokens)
                // 获取句子成分列表
                tokenList = tokens
                loadingView.visibility = View.GONE
                // 处理每个成分
                for (token in tokens) {
                    // 生成一个成分View
                    val v = View.inflate(this@SentenceActivity, R.layout.item_sentence_tango, null)
                    // 写法
                    val writingItem = v.findViewById<TextView>(R.id.writingView)
                    writingItem.typeface = typeface
                    writingItem.text = token.surface
                    // 读音
                    val readingItem = v.findViewById<TextView>(R.id.readingView)
                    readingItem.typeface = typeface
                    val reading = token.reading // 读法
                    val surface = token.surface // 写法
                    if ("*" != reading && reading != surface) {
                        // 若读法与写法不同（写法中含有汉字）
                        // 将片假名转换为平假名
                        val hiraReading = SentenceUtil.convertKana(reading)
                        if (hiraReading != surface) {
                            readingItem.text = hiraReading
                        } else {
                            readingItem.text = " "
                        }
                    } else {
                        readingItem.text = " "
                    }

                    // 词性
                    val partItem = v.findViewById<TextView>(R.id.partView) as TextView
                    partItem.typeface = typeface
                    partItem.text = token.partOfSpeechLevel1

                    // 点击成分时显示对应详细信息
                    val hintItem = v.findViewById<TextView>(R.id.hintView) as TextView
                    val s = token.surface + "\n" +
                            "品詞:" + token.partOfSpeechLevel1 + "\n" +
                            "品詞細分1:" + token.partOfSpeechLevel2 + "\n" +
                            "品詞細分2:" + token.partOfSpeechLevel3 + "\n" +
                            "品詞細分3:" + token.partOfSpeechLevel4 + "\n" +
                            "活用型:" + token.conjugationType + "\n" +
                            "活用形:" + token.conjugationForm + "\n" +
                            "基本形:" + token.baseForm + "\n" +
                            "読み:" + token.reading + "\n" +
                            "発音:" + token.pronunciation + "\n"
                    hintItem.text = s
                    v.setOnClickListener { hintView.text = hintItem.text }
                    // 长按将基本形复制到剪贴板
                    v.setOnLongClickListener {
                        hintView.text = hintItem.text
                        StrUtil.copyToClipboard(this@SentenceActivity, token.baseForm)
                        // 震动提示
                        if (DataManager.vibratorStatus) {
                            VibratorUtil.vibrate(this@SentenceActivity,
                                    TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME)
                        }
                        showToast("已复制到剪切板")
                        true
                    }

                    // 将句子成分添加到流式布局
                    sentenceLayout.addView(v)
                }
            }
        }
        task?.execute()

    }

    override fun setListener() {

    }

    override fun processLogic(savedInstanceState: Bundle?) {
        setJapaneseFont() // 设置日文字体
    }

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        // 获取保存的字体设置
        val title = DataManager.japaneseFontTitle
        val font = TangoConstants.JAPANESE_FONT_MAP[title]
        // 获取设置的字体
        val mgr = assets
        typeface = Typeface.DEFAULT
        if (font != null) {
            typeface = Typeface.createFromAsset(mgr, font)
        }
        // 为日文设置字体
        sentenceView.typeface = typeface
        hintView.typeface = typeface
    }

    override fun onDestroy() {
        super.onDestroy()
        // 中断分词任务
        task?.cancel(true)
    }

}
