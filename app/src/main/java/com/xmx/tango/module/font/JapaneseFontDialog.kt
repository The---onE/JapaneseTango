package com.xmx.tango.module.font

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

import com.xmx.tango.R
import com.xmx.tango.base.dialog.BaseDialog
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.TangoConstants

import org.greenrobot.eventbus.EventBus

/**
 * Created by The_onE on 2017/5/19.
 * 选择日文字体对话框
 */
class JapaneseFontDialog : BaseDialog() {
    private var fontList: ListView? = null
    private var keyArray = arrayOf<String>()

    private class ViewHolder {
        var fontView: TextView? = null
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.dialog_japanese_font, container)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        fontList = view.findViewById(R.id.listFont)
        // 获取可以设置的字体
        keyArray = TangoConstants.JAPANESE_FONT_MAP.keys.toTypedArray()
        val adapter = object : BaseAdapter() {
            override fun getCount(): Int = keyArray.size

            override fun getItem(i: Int): Any = keyArray[i]

            override fun getItemId(i: Int): Long = i.toLong()

            override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
                val v: View
                val holder: ViewHolder
                if (view == null) {
                    v = LayoutInflater.from(mContext).inflate(R.layout.item_japanese_font, viewGroup, false)
                    holder = ViewHolder()
                    holder.fontView = v.findViewById(R.id.itemFont)
                    v.tag = holder
                } else {
                    v = view
                    holder = v.tag as ViewHolder
                }
                // 设置测试文本
                holder.fontView?.text = "あいうえお 日本語"
                // 设置字体
                val mgr = mContext?.assets
                val font = TangoConstants.JAPANESE_FONT_MAP[keyArray[i]]
                var tf = Typeface.DEFAULT
                if (font != null) {
                    tf = Typeface.createFromAsset(mgr, font)
                }
                holder.fontView?.typeface = tf
                return v
            }
        }
        fontList?.adapter = adapter
    }

    override fun setListener(view: View) {
        // 点击选项设置对应的字体
        fontList?.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            DataManager.japaneseFontTitle = keyArray[i]
            EventBus.getDefault().post(JapaneseFontChangeEvent())
            dismiss()
        }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {

    }
}
