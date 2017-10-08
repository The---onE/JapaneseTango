package com.xmx.tango.module.crud

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        AlertDialog.Builder(mContext)
                .setTitle("操作")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(items) { _, i ->
                    // 若再次选择同一排序字段，则改变顺序或倒序
                    if (TangoManager.getInstance().order == orders[i]) {
                        TangoManager.getInstance().ascFlag = !TangoManager.getInstance().ascFlag
                    } else {
                        TangoManager.getInstance().ascFlag = true
                        TangoManager.getInstance().order = orders[i]
                    }

                    EventBus.getDefault().post(TangoListChangeEvent())
                }
                .setNegativeButton("取消", null).show()
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.dialog_search_tango, container, false)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 初始化编辑框
        writingView = view.findViewById(R.id.writingView) as EditText
        pronunciationView = view.findViewById(R.id.pronunciationView) as EditText
        meaningView = view.findViewById(R.id.meaningView) as EditText
        partOfSpeechView = view.findViewById(R.id.partOfSpeechView) as EditText
        typeView = view.findViewById(R.id.typeView) as EditText
        // 根据本机保存的数据设置编辑框
        writingView?.setText(TangoManager.getInstance().writing)
        pronunciationView?.setText(TangoManager.getInstance().pronunciation)
        meaningView?.setText(TangoManager.getInstance().meaning)
        partOfSpeechView?.setText(TangoManager.getInstance().partOfSpeech)
        typeView?.setText(TangoManager.getInstance().type)
    }

    override fun setListener(view: View) {
        // 打开排序对话框
        view.findViewById(R.id.btnSort).setOnClickListener { orderTango() }
        // 进行筛选
        view.findViewById(R.id.btnSearch).setOnClickListener {
            val writing = writingView?.text.toString()
            val pronunciation = pronunciationView?.text.toString()
            val meaning = meaningView?.text.toString()
            val partOfSpeech = partOfSpeechView?.text.toString()
            val type = typeView?.text.toString()
            // 在单语管理器中进行筛选
            TangoManager.getInstance().writing = writing
            TangoManager.getInstance().pronunciation = pronunciation
            TangoManager.getInstance().meaning = meaning
            TangoManager.getInstance().partOfSpeech = partOfSpeech
            TangoManager.getInstance().type = type
            // 将筛选条件保存在本机
            val dm = DataManager.getInstance()
            dm.setSearchValue("writing", writing)
            dm.setSearchValue("pronunciation", pronunciation)
            dm.setSearchValue("meaning", meaning)
            dm.setSearchValue("partOfSpeech", partOfSpeech)
            dm.setSearchValue("type", type)

            EventBus.getDefault().post(TangoListChangeEvent())
            dismiss()
        }
        // 取消筛选
        view.findViewById(R.id.btnCancel).setOnClickListener { dismiss() }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {

    }
}
