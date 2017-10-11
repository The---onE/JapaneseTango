package com.xmx.tango.module.imp

import com.xmx.tango.module.tango.Tango
import com.xmx.tango.utils.ExceptionUtil
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.*
import org.apache.commons.csv.CSVPrinter
import java.util.ArrayList


/**
 * Created by The_onE on 2017/10/10.
 * CSV相关工具
 */
object CsvUtil {
    private val CHARSET = "UTF-8"

    // 各字段所在下标
    val WRITING = 0
    val PRONUNCIATION = 1
    val MEANING = 2
    val TONE = 3
    val PART_OF_SPEECH = 4
    val IMAGE = 5
    val VOICE = 6
    val SCORE = 7
    val FREQUENCY = 8
    val ADD_TIME = 9
    val LAST_TIME = 10
    val FLAGS = 11
    val DEL_FLAG = 12
    val TYPE = 13

    /**
     * 解析CSV文件
     * @param path 文件路径
     * @return CSV记录列表
     */
    fun parseFile(path: String): List<CSVRecord> {
        // 打开文件
        val isr = InputStreamReader(FileInputStream(path), CHARSET)
        // 生成解析器
        val format = CSVFormat.DEFAULT
        val parser = CSVParser(isr, format)
        // 解析出记录
        val records = parser.records
        parser.close()
        return records
    }

    /**
     * 导出单语到CSV文件
     * @param path 文件路径
     * @param tangoList 单语列表
     * @param personalFlag 是否包含个人信息
     * @return 是否导出成功
     */
    fun exportTango(path: String, tangoList: List<Tango>, personalFlag: Boolean): Boolean {
        // 将单语列表转化为记录列表
        val items = convertTango(tangoList, personalFlag)
        // 建记录写入文件
        return exportFile(items, path)
    }

    /**
     * 将单语列表转化为用于保存在CSV文件的记录列表
     * @param tangoList 单语列表
     * @param personalFlag 是否包含个人信息
     * @return 单语信息记录列表
     */
    private fun convertTango(tangoList: List<Tango>, personalFlag: Boolean): List<Array<String>> {
        val items = ArrayList<Array<String>>()
        for (tango in tangoList) {
            val strings = arrayOf<String>(tango.writing, //0
                    tango.pronunciation, //1
                    tango.meaning, //2
                    tango.tone.toString(), //3
                    tango.partOfSpeech, //4
                    tango.image, //5
                    tango.voice, // 6
                    tango.score.toString(), //7
                    tango.frequency.toString(), //8
                    tango.addTime.time.toString(), //9
                    tango.lastTime.time.toString(), //10
                    tango.flags, //11
                    tango.delFlag.toString(), //12
                    tango.type //13
            )
            if (!personalFlag) {
                // 若不包含个人信息，则清除数据
                strings[SCORE] = "0" //Score
                strings[FREQUENCY] = "0" //Frequency
                strings[ADD_TIME] = "0" //AddTime
                strings[LAST_TIME] = "0" //LastTime
                strings[FLAGS] = "" //Flags
                strings[DEL_FLAG] = "0" //DelFlag
                strings[TYPE] = "" //Type
            }
            items.add(strings)
        }
        return items
    }

    /**
     * 将记录列表写入CSV文件
     * @param items 记录列表
     * @param path 文件路径
     * @return 是否导出成功
     */
    private fun exportFile(items: List<Array<String>>, path: String): Boolean {
        try {
            // 打开文件
            val osw = OutputStreamWriter(FileOutputStream(path), CHARSET)
            // 创建打印器
            val format = CSVFormat.DEFAULT
            val printer = CSVPrinter(osw, format)
            // 将记录写入文件
            for (item in items) {
                for (i in item) {
                    printer.print(i)
                }
                printer.println()
            }
            printer.flush()
            printer.close()
            return true
        } catch (e: Exception) {
            ExceptionUtil.normalException(e)
        }
        return false
    }
}
