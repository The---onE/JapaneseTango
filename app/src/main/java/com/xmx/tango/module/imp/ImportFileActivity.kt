package com.xmx.tango.module.imp

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.xmx.tango.R
import com.xmx.tango.base.activity.BaseTempActivity
import com.xmx.tango.utils.CsvUtil
import kotlinx.android.synthetic.main.activity_import_file.*

import java.util.ArrayList

/**
 * Created by The_onE on 2017/10/10.
 * 从文件导入单语Activity
 */
class ImportFileActivity : BaseTempActivity() {

    companion object {
        private val CHOOSE_FILE_RESULT = 1
    }

    private var fileFlag = false // 是否已选文件
    private var filePath: String? = null // 选取文件路径

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHOOSE_FILE_RESULT && resultCode == Activity.RESULT_OK) {
            // 成功选取文件
            val uri = data?.data
            uri?.apply {
                // 设置选取文件
                filePath = this.path
                filePathView.text = filePath
                fileFlag = true
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_import_file)
        // 设置标题
        title = "文件导入"
    }

    override fun setListener() {
        btnChooseFile.setOnClickListener {
            // 点击选取文件
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 调用系统文件选取
            startActivityForResult(intent, CHOOSE_FILE_RESULT)
        }

        btnImportFile.setOnClickListener {
            // 点击导入按钮
            if (fileFlag) {
                filePath?.apply {
                    // 若已选文件
                    val path = when {
                    // 处理文件路径
                        this.contains("primary:") -> {
                            val split = this.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                            val fileType = split[0]
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
                    // 解析CSV文件
                    parseCsv(path)
                }
            } else {
                showToast("未选择文件")
            }
        }
    }

    override fun processLogic(savedInstanceState: Bundle?) {}

    /**
     * 解析CSV文件，解析成功后弹出导入对话框
     * @param filePath CSV文件路径
     */
    private fun parseCsv(filePath: String) {
        try {
            // 在对话框中显示给用户的字符串数组
            val dialogStrings = ArrayList<String>()
            // 导入单语统一设置的类型，若为null则保持各单语原类型
            val type = typeView.text.toString().trim()

            val records = CsvUtil.parseFile(filePath)
            for (record in records) {
                val size = record.size()
                if (size >= 3) {
                    // 添加到列表用于对话框处理
                    dialogStrings.add(record[CsvUtil.WRITING] +
                            ":${record[CsvUtil.PRONUNCIATION]}" +
                            "|${record[CsvUtil.MEANING]}")
                }
            }
            // 显示导入对话框提示导入
            ImportUtil.showFileDialog(dialogStrings, filePath, type, this@ImportFileActivity)
        } catch (e: Exception) {
            filterException(e)
        }
    }
}
