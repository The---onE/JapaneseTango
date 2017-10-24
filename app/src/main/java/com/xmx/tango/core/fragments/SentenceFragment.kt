package com.xmx.tango.core.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView

import com.xmx.tango.R
import com.xmx.tango.base.fragment.BaseFragment
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.font.JapaneseFontChangeEvent
import com.xmx.tango.module.sentence.LrcParser
import com.xmx.tango.module.sentence.SentenceActivity
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.utils.ExceptionUtil
import kotlinx.android.synthetic.main.fragment_sentence.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

/**
 * Created by The_onE on 2017/10/16.
 * 句子页Fragment
 */
class SentenceFragment : BaseFragment() {

    private var adapter = SentenceAdapter() // 句子列表适配器
    private val sentences = ArrayList<String>() // 句子列表
    private var typeface: Typeface? = null // 日文字体

    private val chooseFileResult = 1 // 选择文件

    /**
     * 句子适配器
     */
    private inner class SentenceAdapter : BaseAdapter() {
        internal inner class ViewHolder {
            var textView: TextView? = null
        }

        override fun getCount(): Int = sentences.size

        override fun getItem(i: Int): Any = sentences[i]

        override fun getItemId(i: Int): Long = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val v : View
            val holder: ViewHolder
            if (view == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_sentence, viewGroup, false)
                holder = ViewHolder()
                holder.textView = v.findViewById(R.id.itemSentence)

                v.tag = holder
            } else {
                v = view
                holder = view.tag as ViewHolder
            }
            holder.textView!!.text = sentences[i]
            if (typeface != null) {
                holder.textView!!.typeface = typeface
            }

            return v
        }
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.fragment_sentence, container, false)

    override fun initView(view: View, savedInstanceState: Bundle?) {
    }

    override fun setListener(view: View) {
        // 分析自定义句子
        btnKuromoji.setOnClickListener {
            val sentence = editSentence.text.toString()
            startSentenceActivity(sentence)
        }
        // 选择文件
        btnChooseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, chooseFileResult)
        }
        // 点击句子进入分词Activity
        sentenceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            startSentenceActivity(sentences[i])
        }
        // 长按句子朗读句子
        sentenceList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
            SpeakTangoManager.speak(context, sentences[i])
            true
        }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {
        // 未加载文件的测试句子
        sentences.add("君はメロディー　メロディー")
        sentences.add("懐かしいハーモニー　ハーモニー")
        sentences.add("好きだよと言えず抑えていた胸の痛み")
        sentences.add("僕のメロディー　メロディー ")
        sentences.add("サビだけを覚えてる")
        sentences.add("若さは切なく")
        sentences.add("輝いた日々が")
        sentences.add("蘇（よみがえ）るよ")

        try {
            // 打开assets中的文件
            val i = activity.assets.open("lrc.lrc")
            val isr = InputStreamReader(i)
            val reader = BufferedReader(isr)
            // 使用解析器解析Lrc文件
            val info = LrcParser.parser(reader)
            // 将解析出的句子添加到列表中
            info.info?.apply {
                sentences.clear()
                for ((_, text) in this) {
                    sentences.add(text)
                }
            }
        } catch (e: IOException) {
            ExceptionUtil.normalException(e)
        }
        // 设置日文字体
        setJapaneseFont()
        // 设置适配器
        sentenceList.adapter = adapter

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == chooseFileResult && resultCode == Activity.RESULT_OK) {
            // 成功选取文件
            if (data?.data != null) {
                val uri = data.data
                val path = uri.path
                path?.apply {
                    // 处理特殊文件路径
                    val filePath = when {
                        this.contains("primary:") -> {
                            val split = this.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            android.os.Environment.getExternalStorageDirectory()
                                    .toString() + "/" + split[1]
                        }
                        this.startsWith("/external_files/") -> {
                            this.replace("^/external_files".toRegex(), android.os.Environment
                                    .getExternalStorageDirectory().toString())
                        }
                        this.startsWith("/external/") -> {
                            this.replace("^/external".toRegex(), android.os.Environment
                                    .getExternalStorageDirectory().toString())
                        }
                        else -> this
                    }

                    try {
                        // 根据文件后缀名处理
                        val prefix = filePath.substring(filePath.lastIndexOf(".") + 1)
                        when (prefix) {
                            "txt" -> {
                                // 文本文件
                                val charset = charsetDetect(filePath)
                                val i = FileInputStream(filePath)
                                val isr = InputStreamReader(i, charset)
                                val reader = BufferedReader(isr)
                                sentences.clear()
                                // 读入每一行
                                var text = ""
                                while (reader.readLine()?.apply { text = this } != null) {
                                    sentences.add(text)
                                }
                                // 更新列表，回到顶部
                                adapter.notifyDataSetChanged()
                                sentenceList.smoothScrollToPosition(0)
                                showToast("打开成功")
                            }
                            "lrc" -> {
                                // 歌词文本文件
                                val charset = charsetDetect(filePath)
                                val i = FileInputStream(filePath)
                                val isr = InputStreamReader(i, charset)
                                val reader = BufferedReader(isr)
                                // 使用解析器解析Lrc文件
                                val info = LrcParser.parser(reader)
                                // 将解析出的句子添加到列表中
                                info.info?.apply {
                                    sentences.clear()
                                    for ((_, text) in this) {
                                        sentences.add(text)
                                    }
                                }
                                // 更新列表，回到顶部
                                adapter.notifyDataSetChanged()
                                sentenceList!!.smoothScrollToPosition(0)
                                showToast("打开成功")
                            }
                            else -> showToast("暂不支持打开该类型文件")
                        }
                    } catch (e: Exception) {
                        ExceptionUtil.normalException(e)
                    }
                }

            }
        }
    }

    /**
     * 打开分词Activity
     */
    private fun startSentenceActivity(sentence: String) {
        val intent = Intent(activity, SentenceActivity::class.java)
        intent.putExtra("sentence", sentence)
        startActivity(intent)
    }

    /**
     * 设置日文字体
     */
    private fun setJapaneseFont() {
        typeface = DataManager.getJapaneseTypeface()
    }

    /**
     * 解析文件编码格式
     */
    @Throws(IOException::class)
    private fun charsetDetect(path: String): String {
        val bin = BufferedInputStream(FileInputStream(path))
        val p = (bin.read() shl 8) + bin.read()
        bin.close()
        // 根据前缀判断编码格式
        return when (p) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 处理日文字体改变事件
     */
    @Subscribe
    fun onEvent(event: JapaneseFontChangeEvent) {
        setJapaneseFont()
        adapter.notifyDataSetChanged()
    }
}
