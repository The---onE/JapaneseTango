package com.xmx.tango.module.crud

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import com.xmx.tango.R
import com.xmx.tango.base.dialog.BaseDialog
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.TangoManager

import org.greenrobot.eventbus.EventBus

/**
 * Created by The_onE on 2016/9/21.
 * 筛选单语对话框
 */
class SearchTangoDialog : BaseDialog() {

    private var writingView: EditText? = null
    private var pronunciationView: EditText? = null
    private var meaningView: EditText? = null
    private var partOfSpeechView: EditText? = null
    private var typeView: EditText? = null

    /**
     * 设置单语排序
     */
    private fun orderTango() {
        // 显示字段名
        val items = arrayOf("ID", "分数", "添加时间", "上次时间")
        // 数据库字段名
        val orders = arrayOf("ID", "Score", "AddTime", "LastTime")
        mContext?.apply {
            AlertDialog.Builder(this)
                    .setTitle("操作")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items) { _, i ->
                        // 若再次选择同一排序字段，则改变顺序或倒序
                        if (TangoManager.order == orders[i]) {
                            TangoManager.ascFlag = !TangoManager.ascFlag
                        } else {
                            TangoManager.ascFlag = true
                            TangoManager.order = orders[i]
                        }

                        EventBus.getDefault().post(TangoListChangeEvent())
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.dialog_search_tango, container, false)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 初始化编辑框
        writingView = view.findViewById(R.id.writingView)
        pronunciationView = view.findViewById(R.id.pronunciationView)
        meaningView = view.findViewById(R.id.meaningView)
        partOfSpeechView = view.findViewById(R.id.partOfSpeechView)
        typeView = view.findViewById(R.id.typeView)
        // 根据本机保存的数据设置编辑框
        writingView?.setText(TangoManager.writing)
        pronunciationView?.setText(TangoManager.pronunciation)
        meaningView?.setText(TangoManager.meaning)
        partOfSpeechView?.setText(TangoManager.partOfSpeech)
        typeView?.setText(TangoManager.type)
    }

    override fun setListener(view: View) {
        // 打开排序对话框
        view.findViewById<Button>(R.id.btnSort).setOnClickListener { orderTango() }
        // 进行筛选
        view.findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val writing = writingView?.text.toString()
            val pronunciation = pronunciationView?.text.toString()
            val meaning = meaningView?.text.toString()
            val partOfSpeech = partOfSpeechView?.text.toString()
            val type = typeView?.text.toString()
            // 在单语管理器中进行筛选
            TangoManager.writing = writing
            TangoManager.pronunciation = pronunciation
            TangoManager.meaning = meaning
            TangoManager.partOfSpeech = partOfSpeech
            TangoManager.type = type
            // 将筛选条件保存在本机
            val dm = DataManager
            dm.setSearchValue("writing", writing)
            dm.setSearchValue("pronunciation", pronunciation)
            dm.setSearchValue("meaning", meaning)
            dm.setSearchValue("partOfSpeech", partOfSpeech)
            dm.setSearchValue("type", type)

            EventBus.getDefault().post(TangoListChangeEvent())
            dismiss()
        }
        // 取消筛选
        view.findViewById<Button>(R.id.btnCancel).setOnClickListener { dismiss() }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {

    }
}
