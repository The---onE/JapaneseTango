package com.xmx.tango.module.crud

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.xmx.tango.base.dialog.BaseDialog
import com.xmx.tango.core.CoreConstants
import com.xmx.tango.R
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoEntityManager

import org.greenrobot.eventbus.EventBus

/**
 * Created by The_onE on 2016/9/15.
 * 编辑单语对话框
 */
class UpdateTangoDialog : BaseDialog() {
    private var tango: Tango? = null

    private var writingView: EditText? = null
    private var pronunciationView: EditText? = null
    private var meaningView: EditText? = null
    private var toneView: EditText? = null
    private var partOfSpeechView: EditText? = null
    private var typeView: EditText? = null

    private var idView: TextView? = null
    private var scoreView: TextView? = null

    /**
     * 初始化对话框
     * @param context 当前上下文
     * @param tango 要编辑的单语
     */
    fun initDialog(context: Context, tango: Tango) {
        super.initDialog(context)
        this.tango = tango
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.dialog_update_tango, container, false)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 初始化编辑框
        writingView = view.findViewById(R.id.writingView) as EditText
        pronunciationView = view.findViewById(R.id.pronunciationView) as EditText
        meaningView = view.findViewById(R.id.meaningView) as EditText
        toneView = view.findViewById(R.id.toneView) as EditText
        partOfSpeechView = view.findViewById(R.id.partOfSpeechView) as EditText
        typeView = view.findViewById(R.id.typeView) as EditText
        idView = view.findViewById(R.id.idView) as TextView
        scoreView = view.findViewById(R.id.scoreView) as TextView
        tango?.apply {
            // 设置原单语信息
            writingView?.setText(this.writing)
            pronunciationView?.setText(this.pronunciation)
            meaningView?.setText(this.meaning)
            toneView?.setText("${this.tone}")
            partOfSpeechView?.setText(this.partOfSpeech)
            typeView?.setText(this.type)

            if (CoreConstants.DEBUG_MODE) {
                // 测试模式显示内部信息
                idView?.text = "${this.id}"
                scoreView?.text = "${this.score}"
            } else {
                idView?.visibility = View.GONE
                scoreView?.visibility = View.GONE
            }
        }
    }

    override fun setListener(view: View) {
        view.findViewById(R.id.btnUpdate).setOnClickListener(View.OnClickListener {
            val writing = writingView?.text.toString()
            val pronunciation = pronunciationView?.text.toString()
            val meaning = meaningView?.text.toString()
            val toneStr = toneView?.text.toString()
            val partOfSpeech = partOfSpeechView?.text.toString()
            val type = typeView?.text.toString()
            // 写法和发音不能同时为空
            if (writing.isBlank() && pronunciation.isBlank()) {
                showToast(R.string.add_error)
                return@OnClickListener
            }
            // 处理音调
            var tone = -1
            if (toneStr != "") {
                try {
                    tone = Integer.parseInt(toneStr)
                } catch (e: Exception) {
                    showToast("音调格式不合法")
                    return@OnClickListener
                }
            }
            // 更新单语
            tango?.apply {
                TangoEntityManager.updateData(this.id,
                        "Writing='$writing'",
                        "Pronunciation='$pronunciation'",
                        "Meaning='$meaning'",
                        "Tone=" + tone,
                        "PartOfSpeech='$partOfSpeech'",
                        "Type='$type'")

                EventBus.getDefault().post(TangoListChangeEvent())
                dismiss()
            }
        })

        view.findViewById(R.id.btnCancel).setOnClickListener { dismiss() }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {

    }
}
