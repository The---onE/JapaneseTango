package com.xmx.tango.core.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.AdapterView

import com.xmx.tango.base.fragment.BaseFragment
import com.xmx.tango.core.CoreConstants
import com.xmx.tango.module.imp.ImportFileActivity
import com.xmx.tango.R
import com.xmx.tango.module.crud.AddTangoActivity
import com.xmx.tango.module.crud.ChooseTangoEvent
import com.xmx.tango.module.font.JapaneseFontChangeEvent
import com.xmx.tango.module.imp.ImportNetActivity
import com.xmx.tango.module.operate.OperateTangoEvent
import com.xmx.tango.module.crud.SearchTangoDialog
import com.xmx.tango.module.speaker.SpeakTangoManager
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.module.tango.TangoAdapter
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoEntityManager
import com.xmx.tango.module.crud.TangoListChangeEvent
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.module.crud.UpdateTangoDialog
import com.xmx.tango.module.verb.VerbDialog
import com.xmx.tango.module.imp.CsvUtil
import com.xmx.tango.utils.Timer
import kotlinx.android.synthetic.main.fragment_tango_list.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by The_onE on 2017/10/17.
 * 单语列表页Fragment
 */
class TangoListFragment : BaseFragment() {
    // 列表适配器
    private val tangoAdapter by lazy {
        TangoAdapter(context, TangoManager.tangoList)
    }
    private var itemDoubleFlag = false // 是否已点击一次，接受双按

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.fragment_tango_list, container, false)

    @SuppressLint("SetTextI18n")
    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 设置列表适配器
        tangoList.adapter = tangoAdapter
        // 在SplashActivity中调用了updateTangoList
        // 显示总数
        countView.text = "${TangoManager.tangoList.size}/${TangoEntityManager.getCount()}"
    }

    override fun setListener(view: View) {
        // 弹出筛选对话框
        btnSearch.setOnClickListener {
            val dialog = SearchTangoDialog()
            dialog.initDialog(context)
            dialog.show(activity.fragmentManager, "SEARCH_TANGO")
        }
        // 弹出操作菜单
        btnOperation.setOnClickListener {
            val items = arrayOf("添加", "导出", "导入", "删除")
            AlertDialog.Builder(context)
                    .setTitle("操作")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items) { _, i ->
                        when (i) {
                            0 -> addTango()
                            1 -> exportTango()
                            2 -> importTango()
                            3 -> deleteTango()
                        }
                    }
                    .setNegativeButton("取消", null)
                    .show()
        }
        // 点击单语
        tangoList.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val tango = tangoAdapter.getItem(i) as Tango
            if (itemDoubleFlag) { // 已点击
                // 双按朗读单语
                itemDoubleFlag = false
                val writing = tango.writing
                if (writing.isNotBlank()) {
                    SpeakTangoManager.speak(context, writing)
                }
            } else {
                itemDoubleFlag = true
                Timer {
                    if (itemDoubleFlag) { // 超时未按下第二次
                        // 执行单按逻辑
                        // 显示动词变形对话框
                        showVerbDialog(tango)
                    }
                    itemDoubleFlag = false
                }.start(ViewConfiguration.getDoubleTapTimeout().toLong(), true)
            }
        }
        // 长按单语提示编辑单语
        tangoList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
            val tango = tangoAdapter.getItem(i) as Tango

            AlertDialog.Builder(context)
                    .setMessage("要编辑该记录吗？")
                    .setTitle("提示")
                    // 编辑
                    .setPositiveButton("编辑") { _, _ ->
                        // 弹出编辑对话框
                        val dialog = UpdateTangoDialog()
                        dialog.initDialog(context, tango)
                        dialog.show(activity.fragmentManager, "UPDATE_TANGO")
                    }
                    // 删除
                    .setNegativeButton("删除") { _, _ ->
                        AlertDialog.Builder(context)
                                .setMessage("确定要删除吗？")
                                .setTitle("提示")
                                .setPositiveButton("删除") { _, _ ->
                                    // 删除单语
                                    TangoEntityManager.deleteById(tango.id)
                                    EventBus.getDefault().post(TangoListChangeEvent())
                                }
                                .setNeutralButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }
                                .show()
                    }
                    .setNeutralButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            true
        }
    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    /**
     * 添加单语
     */
    private fun addTango() {
        // 弹出添加单语对话框
        startActivity(AddTangoActivity::class.java)
    }

    /**
     * 导出单语
     */
    private fun exportTango() {
        // 弹出导出单语对话框
        AlertDialog.Builder(context)
                .setMessage("要导出数据吗？")
                .setTitle("提示")
                .setPositiveButton("包含学习信息") { _, _ -> exportTango(true) }
                .setNegativeButton("仅导出単語") { _, _ -> exportTango(false) }
                .setNeutralButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    /**
     * 导入单语
     */
    private fun importTango() {
        // 弹出导入单语对话框
        AlertDialog.Builder(context)
                .setMessage("要导入数据吗？")
                .setTitle("提示")
                .setPositiveButton("文件导入") { _, _ -> startActivity(ImportFileActivity::class.java) }
                .setNegativeButton("网络导入") { _, _ -> startActivity(ImportNetActivity::class.java) }
                .setNeutralButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    /**
     * 删除单语
     */
    private fun deleteTango() {
        // 弹出确认删除对话框
        AlertDialog.Builder(context)
                .setMessage("确定要删除列出的数据吗？")
                .setTitle("提示")
                .setPositiveButton("删除") { _, _ ->
                    // 批量删除列出的所有单语
                    val tangoList = TangoManager.tangoList
                    val ids = tangoList.map { it.id }
                    if (TangoEntityManager.deleteByIds(ids)) {
                        showToast("删除成功")
                        EventBus.getDefault().post(TangoListChangeEvent())
                    }
                }
                .setNeutralButton("取消") { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    /**
     * 导出单语
     * @param personalFlag 是否包含个人数据
     */
    private fun exportTango(personalFlag: Boolean) {
        val list = TangoManager.tangoList
        // 生成导出路径
        val dir = Environment.getExternalStorageDirectory().toString() + CoreConstants.FILE_DIR
        val filename = "/export.csv"
        if (CsvUtil.exportTango(dir + filename, list, personalFlag)) {
            showToast("成功导出至:" + dir + filename)
        } else {
            showToast("导出失败")
        }
    }

    /**
     * 更新单语列表
     */
    @SuppressLint("SetTextI18n")
    private fun updateTangoList() {
        // 根据筛选条件查询列表
        TangoManager.updateTangoList()
        val tangoList = TangoManager.tangoList
        // 更新列表数据
        tangoAdapter.updateList(tangoList)
        // 显示筛选条数和总条数
        countView.text = "${tangoList.size}/${TangoEntityManager.getCount()}"
    }

    /**
     * 显示动词变形对话框
     */
    private fun showVerbDialog(tango: Tango) {
        val part = tango.partOfSpeech
        if (part.isNotBlank()) {
            if (part.contains(TangoConstants.VERB_FLAG)) {
                // 单语词性为动词
                val verb = tango.writing
                val type = when (part) {
                    TangoConstants.VERB1_FLAG -> 1
                    TangoConstants.VERB2_FLAG -> 2
                    TangoConstants.VERB3_FLAG -> 3
                    else -> 0
                }
                // 弹出动词变形对话框
                if (type > 0) {
                    val dialog = VerbDialog()
                    dialog.initDialog(context, verb, type)
                    dialog.show(activity.fragmentManager, "VERB")
                }
            }
        }
    }

    /**
     * 处理单语列表变更事件
     */
    @Subscribe
    fun onEvent(event: TangoListChangeEvent) {
        updateTangoList()
    }

    /**
     * 处理操作单语事件
     */
    @Subscribe
    fun onEvent(event: OperateTangoEvent) {
    }

    /**
     * 处理选择单语事件
     */
    @Subscribe
    fun onEvent(event: ChooseTangoEvent) {
        // 在列表中查找ID
        val i = TangoManager.tangoList
                .takeWhile { it.id != event.tango.id }
                .count()
        // 在列表中选中选择项
        if (i < TangoManager.tangoList.size) {
            tangoList.setSelection(i)
        }
    }

    /**
     * 处理改变日语字体事件
     */
    @Subscribe
    fun onEvent(event: JapaneseFontChangeEvent) {
        tangoAdapter.notifyDataSetChanged()
    }
}
