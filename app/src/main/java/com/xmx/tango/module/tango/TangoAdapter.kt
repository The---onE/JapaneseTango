package com.xmx.tango.module.tango

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.xmx.tango.common.data.DataManager
import com.xmx.tango.R
import com.xmx.tango.common.data.BaseEntityAdapter

import java.text.SimpleDateFormat

/**
 * Created by The_onE on 2016/3/27.
 * 单语列表适配器
 */
class TangoAdapter(context: Context, data: List<Tango>) : BaseEntityAdapter<Tango>(context, data) {

    internal class ViewHolder {
        var writing: TextView? = null
        var pronunciation: TextView? = null
        var tone: TextView? = null
        var meaning: TextView? = null
        var part: TextView? = null
        var time: TextView? = null
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cv = convertView
        val holder: ViewHolder
        if (cv == null) {
            // 生成ViewHolder
            cv = LayoutInflater.from(mContext).inflate(R.layout.item_tango, parent, false)
            holder = ViewHolder()
            holder.writing = cv.findViewById(R.id.itemWriting) as TextView
            holder.pronunciation = cv.findViewById(R.id.itemPronunciation) as TextView
            holder.tone = cv.findViewById(R.id.itemTone) as TextView
            holder.meaning = cv.findViewById(R.id.itemMeaning) as TextView
            holder.part = cv.findViewById(R.id.itemPart) as TextView
            holder.time = cv.findViewById(R.id.itemTime) as TextView

            cv.tag = holder
        } else {
            holder = cv.tag as ViewHolder
        }

        if (position < mData.size) {
            // 设置字体
            val title = DataManager.japaneseFontTitle
            val font = TangoConstants.JAPANESE_FONT_MAP[title]
            val mgr = mContext.assets
            var tf = Typeface.DEFAULT
            if (font != null) {
                tf = Typeface.createFromAsset(mgr, font)
            }
            holder.pronunciation?.typeface = tf
            holder.writing?.typeface = tf

            // 设置写法
            val tango = mData[position]
            holder.writing?.text = tango.writing
            // 设置发音
            holder.pronunciation?.text = tango.pronunciation
            // 获取音调对应显示的字符
            if (tango.tone >= 0 && tango.tone < TangoConstants.TONES.size) {
                holder.tone?.text = TangoConstants.TONES[tango.tone]
            } else {
                holder.tone?.text = ""
            }
            // 设置词性显示方式
            if (tango.partOfSpeech.isNotBlank()) {
                holder.part?.text = "[${tango.partOfSpeech}]"
            } else {
                holder.part?.text = ""
            }
            // 设置解释
            holder.meaning?.text = tango.meaning
            // 设置时间
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timeString = df.format(tango.addTime)
            holder.time?.text = timeString
        } else {
            holder.writing?.text = "加载失败"
        }

        return cv!!
    }
}