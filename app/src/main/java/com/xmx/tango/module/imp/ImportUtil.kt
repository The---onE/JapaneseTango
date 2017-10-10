package com.xmx.tango.module.imp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog

import com.xiaoleilu.hutool.lang.Conver
import com.xmx.tango.module.tango.Tango
import com.xmx.tango.utils.CsvUtil
import com.xmx.tango.utils.StrUtil
import org.apache.commons.csv.CSVRecord

import java.util.Date

/**
 * Created by The_onE on 2017/7/28.
 * 导入单语工具类
 */
object ImportUtil {
    /**
     * 显示识别出单语的对话框，点击确认调用Service进行导入
     * @param dialogStrings 在对话框中显示给用户的字符串数组
     * @param path 要导入的CSV文件路径
     * @param type 导入单语统一设置的类型，若为null则保持各单语原类型
     * @param context 当前上下文
     */
    fun showFileDialog(dialogStrings: List<String>,
                       path: String,
                       type: String,
                       context: Context) {
        val array = dialogStrings.toTypedArray()
        AlertDialog.Builder(context)
                .setTitle("识别出的単語")
                .setItems(array, null)
                .setPositiveButton("导入") { _, _ ->
                    StrUtil.showToast(context, "正在导入，请稍后")
                    val service = Intent(context, ImportFileService::class.java)
                    service.putExtra("path", path)
                    service.putExtra("type", type)
                    context.startService(service)
                }
                .setNegativeButton("取消", null)
                .show()
    }

    /**
     * 将CSV记录转化为单语实体
     * @param record 一条CSV记录
     * @param type 单语统一设置的类型，若为null则保持各单语原类型
     * @return 单语实体
     */
    private fun convertTango(record: CSVRecord, type: String): Tango {
        val tango = Tango()
        try {
            tango.writing = record[CsvUtil.WRITING]
            tango.pronunciation = record[CsvUtil.PRONUNCIATION]
            tango.meaning = record[CsvUtil.MEANING]
            tango.tone = Conver.toInt(record[CsvUtil.TONE], -1)
            tango.partOfSpeech = record[CsvUtil.PART_OF_SPEECH]
            tango.image = record[CsvUtil.IMAGE]
            tango.voice = record[CsvUtil.VOICE]
            tango.score = Conver.toInt(record[CsvUtil.SCORE], 0)
            tango.frequency = Conver.toInt(record[CsvUtil.FREQUENCY], 0)
            tango.addTime = Date(Conver.toLong(record[CsvUtil.ADD_TIME], 0L))
            tango.lastTime = Date(Conver.toLong(record[CsvUtil.LAST_TIME], 0L))
            tango.flags = record[CsvUtil.FLAGS]
            tango.delFlag = Conver.toInt(record[CsvUtil.DEL_FLAG], 0)
            tango.type = record[CsvUtil.TYPE]
        } catch (e: IndexOutOfBoundsException) {
        } finally {
            if (!StrUtil.isBlank(type)) {
                tango.type = type
            }
            if (tango.addTime.time == 0L) {
                tango.addTime = Date()
            }
        }

        return tango
    }

    /**
     * 将CSV记录列表转化为单语实体列表
     * @param list CSV记录列表
     * @param type 单语统一设置的类型，若为null则保持各单语原类型
     * @return 单语实体列表
     */
    fun convertTangoList(list: List<CSVRecord>, type: String): List<Tango> =
            list.map { convertTango(it, type) }
}
