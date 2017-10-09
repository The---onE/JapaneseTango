package com.xmx.tango.module.tango

import java.util.ArrayList
import java.util.Random

/**
 * Created by The_onE on 2016/9/13.
 * 单语管理器
 */
object TangoManager {
    // 展示列表筛选条件
    var writing: String? = null
    var pronunciation: String? = null
    var meaning: String? = null
    var partOfSpeech: String? = null
    var type: String? = null

    // 更新列表排序条件
    var order = "ID"
    var ascFlag = true

    private var version = System.currentTimeMillis() // 当前版本
    var tangoList: MutableList<Tango> = ArrayList() // 用于显示的单语列表
        private set
    var waitingList: MutableList<Tango> = ArrayList() // 待选单语列表
        private set
    private val random = Random()

    private var index = 0 // 当前选择的单语在待选列表中的索引
    private val tempTangos = ArrayList<Tango>() // 待选列表中没有单语时显示的临时单语

    init {
        // 初始化临时列表
        val t1 = Tango()
        t1.id = -1
        t1.writing = "愛"
        t1.pronunciation = "あい"
        t1.tone = 1
        tempTangos.add(t1)

        val t2 = Tango()
        t2.id = -2
        t2.writing = "大切"
        t2.pronunciation = "たいせつ"
        t2.tone = 0
        t2.meaning = "重要,珍贵"
        t2.partOfSpeech = "形容动词"
        tempTangos.add(t2)

        val t3 = Tango()
        t3.id = -3
        t3.writing = "ありがとうございます"
        t3.pronunciation = "ありがとうございます"
        t3.meaning = "谢谢"
        t3.partOfSpeech = "惯用语"
        tempTangos.add(t3)

        val t4 = Tango()
        t4.id = -4
        t4.writing = "よろしくお願い申し上げます"
        t4.pronunciation = "よろしくおねがいもうしあげます"
        t4.meaning = "请多关照"
        t4.partOfSpeech = "惯用语"
        tempTangos.add(t4)
    }

    /**
     * 从数据库获取用于展示的符合筛选条件的单语数据
     * @return 当前版本
     */
    fun updateTangoList(): Long {
        val manager = TangoEntityManager
        // 根据设置更改筛选条件
        val con = ArrayList<String>()
        if (writing != null && writing != "") {
            con.add("Writing like '%$writing%'")
        }
        if (pronunciation != null && pronunciation != "") {
            con.add("Pronunciation like '%$pronunciation%'")
        }
        if (meaning != null && meaning != "") {
            con.add("Meaning like '%$meaning%'")
        }
        if (partOfSpeech != null && partOfSpeech != "") {
            con.add("PartOfSpeech like '%$partOfSpeech%'")
        }
        if (type != null && type != "") {
            con.add("Type like '%$type%'")
        }

        val array = con.toTypedArray<String?>()
        // 重新查询单语列表
        tangoList = manager.selectByCondition(order, ascFlag, *array)

        version++
        return version
    }

    /**
     * 更新待选列表
     * @param reviewFlag   是否为复习状态
     * @param maxFrequency 复习频率上限
     * @param limit        选取单语上限
     * @return 待选列表
     */
    fun updateWaitingList(reviewFlag: Boolean, maxFrequency: Int, limit: Int = TangoConstants.DEFAULT_LIMIT): List<Tango>? {
        waitingList = TangoEntityManager
                .selectTangoScoreAsc(limit, reviewFlag, maxFrequency) ?: ArrayList()
        return waitingList
    }

    /**
     * 在待选列表中添加单语
     * @param tango 待添加单语
     */
    fun addToWaitingList(tango: Tango?) {
        tango?.apply {
            // 若待选列表中已存在则不添加
            if (waitingList.any { it.id == this.id }) return
            waitingList.add(this)
        }
    }

    /**
     * 在待选列表中删除单语
     * @param tango 待删除单语
     */
    fun removeFromWaitingList(tango: Tango?) {
        waitingList.remove(tango)
    }

    /**
     * 随机选取单语
     * @param reviewFlag   是否为复习状态
     * @param maxFrequency 复习频率上限
     * @param prevTango    上一个单语
     * @param missionFlag  是否为任务模式
     * @return 随机选取的单语
     */
    fun randomTango(reviewFlag: Boolean, maxFrequency: Int, prevTango: Tango?,
                    missionFlag: Boolean): Tango? {
        if (!missionFlag) {
            // 非任务模式每次刷新列表
            updateWaitingList(reviewFlag, maxFrequency)
        }
        var temp: Tango? = null
        waitingList.apply {
            if (this.isNotEmpty()) {
                // 从待选列表随机选取
                val size = this.size
                index = random.nextInt(size)
                temp = this[index]
                // 若选取的单语和上次相同则选取下一个
                if (prevTango != null && temp?.id == prevTango.id) {
                    index = ++index % size
                    temp = this[index]
                }
            } else {
                //TODO 待选列表没有符合条件的单语
                index = random.nextInt(tempTangos.size)
                temp = tempTangos[index]
                if (prevTango != null && temp?.id == prevTango.id) {
                    index = ++index % tempTangos.size
                    temp = tempTangos[index]
                }
            }
        }
        return temp
    }
}
