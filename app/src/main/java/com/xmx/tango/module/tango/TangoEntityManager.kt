package com.xmx.tango.module.tango

import com.xmx.tango.common.data.DataManager
import com.xmx.tango.common.data.sql.BaseSQLEntityManager
import com.xmx.tango.utils.StrUtil

import java.util.ArrayList

/**
 * Created by The_onE on 2016/9/13.
 * 单语数据库实体管理器
 */
object TangoEntityManager : BaseSQLEntityManager<Tango>() {

    init {
        tableName = "Tango"
        entityTemplate = Tango()
        openDatabase()
    }

    /**
     * 按分数升序排列，选取单语
     * @param count 选取的个数
     * @param reviewFlag 是否只选取学习过的单语
     * @param maxFrequency 最大复习系数
     */
    fun selectTangoScoreAsc(count: Int, reviewFlag: Boolean, maxFrequency: Int): List<Tango>? {
        if (!checkDatabase()) {
            return null
        }
        // 筛选条件
        val conditions = ArrayList<String>()
        // 筛选类型
        val type = DataManager.tangoType
        if (type != "") {
            conditions.add("Type like '%$type%'")
        }
        // 筛选词性
        val part = DataManager.partOfSpeech
        if (part != "") {
            conditions.add("PartOfSpeech like '%$part%'")
        }
        // 筛选复习系数
        if (maxFrequency >= 0) {
            conditions.add("Frequency <= " + maxFrequency)
        }
        // 是否只选取学习过的单语
        val reviewStr: String
        if (reviewFlag) {
            reviewStr = "Frequency desc, "
        } else {
            reviewStr = ""
            conditions.add("Frequency >= 0")
        }
        // 拼接条件SQL语句
        var conStr = ""
        if (conditions.size > 0) {
            conStr = " where "
            conStr += StrUtil.join(conditions, " and ")
        }
        // 从数据库中查询
        val cursor = database?.rawQuery("select * from $tableName $conStr " +
                "order by $reviewStr Score asc, LastTime asc limit $count", null)
        return convertToEntities(cursor)
    }
}
