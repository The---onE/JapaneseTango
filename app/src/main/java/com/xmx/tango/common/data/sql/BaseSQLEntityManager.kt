package com.xmx.tango.common.data.sql

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import com.xmx.tango.core.CoreConstants
import com.xmx.tango.utils.ExceptionUtil

import java.io.File
import java.util.ArrayList

/**
 * Created by The_onE on 2017/10/14.
 * SQLite数据库管理基类，提供常用的增删改查功能
 */
abstract class BaseSQLEntityManager<Entity : ISqlEntity> {
    // 管理数据库连接
    protected var database: SQLiteDatabase? = null
    // 数据变更时会更新版本
    var version = System.currentTimeMillis()
        protected set
    // 是否已打开数据库
    private var openFlag = false

    // 子类构造函数中初始化下列变量！初始化后再调用openDatabase方法
    protected lateinit var tableName: String // 表名
    protected lateinit var entityTemplate: Entity // 空模版，不需要数据

    /**
     * 打开数据库文件
     * @param filename 若为空则使用默认数据库文件，否则使用默认目录中的给定文件
     * @return 是否打开成功
     */
    private fun openSQLFile(filename: String? = null): Boolean {
        // 打开或创建数据库目录
        val d = android.os.Environment.getExternalStorageDirectory().toString() +
                CoreConstants.DATABASE_DIR
        val dir = File(d) // 数据库目录路径
        val flag = dir.exists() || dir.mkdirs() // 若目录不存在则创建目录

        // 打开数据库文件
        if (flag) {
            // 若无给定文件则使用默认数据库文件，否则使用默认目录中的给定文件
            var sqlFile = android.os.Environment.getExternalStorageDirectory().toString()
            sqlFile += if (filename == null) {
                CoreConstants.DATABASE_FILE
            } else {
                CoreConstants.DATABASE_DIR + "/" + filename + ".db"
            }

            val file = File(sqlFile) // 数据库文件路径
            database = SQLiteDatabase.openOrCreateDatabase(file, null) // 打开数据库文件
            if (database == null) {
                ExceptionUtil.normalException(SQLException("创建文件失败"))
                return false
            }
            version++
            return true
        } else {
            ExceptionUtil.normalException(SQLException("创建目录失败"))
            return false
        }
    }

    /**
     * 打开数据库及对应表
     * @param filename 若为空则使用默认数据库文件，否则使用默认目录中的给定文件
     * @return 是否打开成功
     */
    fun openDatabase(filename: String? = null): Boolean {
        // 打开数据库文件
        if (!openSQLFile(filename)) {
            return false
        }
        openFlag = if (database != null) {
            // 若不存在表则创建表
            val createSQL = "create table if not exists " +
                    "$tableName(${entityTemplate.tableFields()})"
            database?.execSQL(createSQL)
            true
        } else {
            false
        }
        return openFlag
    }

    /**
     * 关闭数据库
     */
    fun closeDatabase() {
        openFlag = false
    }

    /**
     * 检查数据库是否打开，若未打开则打开
     * @return 数据库是否已打开
     */
    protected fun checkDatabase(): Boolean = openFlag || openDatabase()

    /**
     * 清空数据表
     */
    fun clearDatabase() {
        if (!checkDatabase()) {
            return
        }
        // 清空表中数据
        val clear = "delete from $tableName"
        database?.execSQL(clear)
        // 自增主键归零
        val zero = "delete from sqlite_sequence where NAME = '$tableName'"
        database?.execSQL(zero)
        // 数据变更
        version++
    }

    /**
     * 插入实体数据
     * @param entity 要插入的数据
     * @return 插入数据的ID
     */
    fun insertData(entity: Entity): Long {
        if (!checkDatabase()) {
            return -1
        }
        // 插入数据
        val flag = database?.insert(tableName, null, entity.getContent()) ?: 0
        if (flag > 0) {
            // 数据变更
            version++
        }
        return flag
    }

    /**
     * 事务操作
     * @param callback 事务执行回调
     * @return 事务是否执行成功
     */
    fun operationInTransaction(callback: TransactionCallback): Boolean {
        if (!checkDatabase()) {
            return false
        }
        var flag = false
        database?.beginTransaction()
        try {
            val total = callback.operation()
            database?.setTransactionSuccessful()
            callback.success(total)
            flag = true
            version++
        } catch (e: Exception) {
            callback.error(e)
        } finally {
            database?.endTransaction()
        }
        return flag
    }

    /**
     * 插入多条实体数据，在同一事务中
     * @param entities 要插入的实体列表
     * @return 是否全部插入成功
     */
    fun insertData(entities: List<Entity>): Boolean {
        return operationInTransaction(object : TransactionCallback() {
            @Throws(Exception::class)
            override fun operation(): Int {
                for (entity in entities) {
                    database?.insert(tableName, null, entity.getContent())
                }
                return entities.size
            }

            override fun success(total: Int) {

            }

            override fun error(e: Exception) {
                ExceptionUtil.normalException(e, null)
            }
        })
    }

    /**
     * 插入多条实体数据，在同一事务中
     * @param entities 要插入的实体列表
     * @param callback 插入回调
     * @return 是否全部插入成功
     */
    fun insertData(entities: List<Entity>, callback: InsertCallback): Boolean {
        return operationInTransaction(object : TransactionCallback() {
            @Throws(Exception::class)
            override fun operation(): Int {
                // 在事务中插入数据，每删除一条进行回调
                var index = 0
                for (entity in entities) {
                    database?.insert(tableName, null, entity.getContent())
                    index++
                    callback.proceeding(index)
                }
                return index
            }

            override fun success(total: Int) {
                callback.success(total)
            }

            override fun error(e: Exception) {
                callback.error(e)
            }
        })
    }

    /**
     * 删除数据
     * @param id 要删除的数据ID
     * @return 大于0表示删除成功，否则删除失败
     */
    fun deleteById(id: Long): Int {
        if (!checkDatabase()) {
            return 0
        }
        // 删除数据
        val res = database?.delete(tableName, "ID = ?", arrayOf(id.toString())) ?: 0
        // 数据变更
        version++
        return res
    }

    /**
     * 删除多条数据，在同一事务中
     * @param ids 要删除的ID列表
     * @return 是否全部删除成功
     */
    fun deleteByIds(ids: List<Long>): Boolean {
        return operationInTransaction(object : TransactionCallback() {
            @Throws(Exception::class)
            override fun operation(): Int {
                for (id in ids) {
                    database?.delete(tableName, "ID = ?", arrayOf(id.toString()))
                }
                return ids.size
            }

            override fun success(total: Int) {

            }

            override fun error(e: Exception) {
                ExceptionUtil.normalException(e, null)
            }
        })
    }

    /**
     * 删除多条数据，在同一事务中
     * @param ids 要删除的ID列表
     * @param callback 删除回调
     * @return 是否全部删除成功
     */
    fun deleteByIds(ids: List<Long>, callback: DeleteCallback): Boolean {
        return operationInTransaction(object : TransactionCallback() {
            @Throws(Exception::class)
            override fun operation(): Int {
                // 在事务中删除数据，每删除一条进行回调
                var index = 0
                for (id in ids) {
                    database?.delete(tableName, "ID = ?", arrayOf(id.toString()))
                    index++
                    callback.proceeding(index)
                }
                return index
            }

            override fun success(total: Int) {
                callback.success(total)
            }

            override fun error(e: Exception) {
                callback.error(e)
            }
        })
    }

    /**
     * 根据云端ID删除数据
     * @param cloudId 要删除数据的云端ID
     * @return 大于0表示删除成功，否则删除失败
     */
    fun deleteByCloudId(cloudId: String): Int {
        if (!checkDatabase()) {
            return 0
        }
        // 删除数据
        val res = database?.delete(tableName, "CLOUD_ID = ?", arrayOf(cloudId)) ?: 0
        // 数据变更
        version++
        return res
    }

    /**
     * 更改数据
     * @param id 要更改的数据ID
     * @param strings 要更改的内容，"KEY1=Value1", "KEY2=Value2"
     */
    fun updateData(id: Long, vararg strings: String) {
        if (!checkDatabase()) {
            return
        }
        // 更新内容
        var content = ""
        if (strings.isNotEmpty()) {
            content = strings.joinToString(prefix = "set ", separator = ", ")
        }
        // 更新数据
        val update = "update $tableName $content where ID = $id"
        database?.execSQL(update)
        // 数据变更
        version++
    }

    /**
     * 根据云端ID更改数据
     * @param cloudId 要更改的数据的云端ID
     * @param strings 要更改的内容，"KEY1=Value1", "KEY2=Value2"
     */
    fun updateData(cloudId: String, vararg strings: String) {
        if (!checkDatabase()) {
            return
        }
        // 更新内容
        var content = ""
        if (strings.isNotEmpty()) {
            content = strings.joinToString(prefix = "set ", separator = ", ")
        }
        // 更新数据
        val update = "update $tableName $content where CLOUD_ID = '$cloudId'"
        database?.execSQL(update)
        // 数据变更
        version++
    }

    /**
     * 将查询数据转化为实体列表
     * @param cursor 查询方法获取的查询结果集
     * @return 转化后的实体列表
     */
    protected fun convertToEntities(cursor: Cursor?): List<Entity>? {
        if (!checkDatabase() || cursor == null) {
            return null
        }
        val entities = ArrayList<Entity>()
        if (cursor.moveToFirst()) {
            // 将每一条数据转化为实体，并加入列表
            do {
                val entity: ISqlEntity = entityTemplate.convertToEntity(cursor)
                entities.add(entity as Entity)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return entities
    }

    /**
     * 将查询数据转化为实体
     * @param cursor 查询方法获取的查询结果
     * @return 转化后的实体
     */
    private fun convertToEntity(cursor: Cursor?): Entity? {
        if (!checkDatabase() || cursor == null) {
            return null
        }
        // 将查询数据转化为实体并返回
        return if (cursor.moveToFirst()) {
            val entity: ISqlEntity = entityTemplate.convertToEntity(cursor)
            entity as Entity
        } else {
            null
        }
    }

    /**
     * 查询全部数据
     * @return 查询出的实体列表
     */
    fun selectAll(): List<Entity>? {
        if (!checkDatabase()) {
            return null
        }
        // 查询全部数据并转化为实体列表
        val cursor = database?.rawQuery("select * from $tableName", null)
        val entities = convertToEntities(cursor)
        cursor?.close()
        return entities
    }

    /**
     * 查询全部数据并排序
     * @param order 排序字段
     * @param ascFlag true为升序，false为降序
     * @return 查询出的实体列表
     */
    fun selectAll(order: String, ascFlag: Boolean): List<Entity>? {
        if (!checkDatabase()) {
            return null
        }
        // 设置排序方式
        val asc: String = if (ascFlag) {
            "asc"
        } else {
            "desc"
        }
        // 查询全部数据并转化为实体列表
        val cursor = database?.rawQuery("select * from $tableName order by $order $asc", null)
        val entities = convertToEntities(cursor)
        cursor?.close()
        return entities
    }

    /**
     * 通过ID查询数据
     * @param id 要查询的ID
     * @return 查询出的实体
     */
    fun selectById(id: Long): Entity? {
        if (!checkDatabase()) {
            return null
        }
        // 查询数据并转化为实体
        val cursor = database?.rawQuery("select * from $tableName where ID=$id", null)
        val entity = convertToEntity(cursor)
        cursor?.close()
        return entity
    }

    /**
     * 通过云端ID查询数据
     * @param cloudId 要查询的云端ID
     * @return 查询出的实体
     */
    fun selectByCloudId(cloudId: String): Entity? {
        if (!checkDatabase()) {
            return null
        }
        // 查询数据并转化为实体
        val cursor = database!!.rawQuery("select * from $tableName where CLOUD_ID='$cloudId'"
                , null)
        val entity = convertToEntity(cursor)
        cursor.close()
        return entity
    }

    /**
     * 查询值在范围内的数据
     * @param data 查询条件字段
     * @param min 最小值
     * @param max 最大值
     * @return 查询出的实体列表
     */
    fun selectAmount(data: String, min: String, max: String): List<Entity>? {
        if (!checkDatabase()) {
            return null
        }
        // 查询符合条件的数据并转化为实体列表
        val cursor = database?.rawQuery("select * from $tableName" +
                " where $data between $min and $max", null)
        val entities = convertToEntities(cursor)
        cursor?.close()
        return entities
    }

    /**
     * 查询符合条件的第一条数据
     * @param order 排序字段
     * @param ascFlag true为升序，false为降序
     * @param condition 查询条件，"KEY1=Value1", "KEY2!=Value2"
     * @return 查询出的实体
     */
    fun selectFirst(order: String, ascFlag: Boolean, vararg condition: String): Entity? {
        if (!checkDatabase()) {
            return null
        }
        // 查询条件
        var content = ""
        if (condition.isNotEmpty()) {
            content = condition.joinToString(prefix = "where ", separator = " and ")
        }
        // 设置排序方式
        val asc: String = if (ascFlag) {
            "asc"
        } else {
            "desc"
        }
        // 查询第一条符合条件的数据并转化为实体
        val cursor = database?.rawQuery("select * from $tableName $content order by $order $asc limit 1", null)
        val entity = convertToEntity(cursor)
        cursor?.close()
        return entity
    }

    /**
     * 根据条件查询数据
     * @param order 排序字段
     * @param ascFlag true为升序，false为降序
     * @param condition 查询条件，"KEY1=Value1", "KEY2!=Value2"
     * @return 查询出的实体列表
     */
    fun selectByCondition(order: String, ascFlag: Boolean, vararg condition: String): List<Entity>? {
        if (!checkDatabase()) {
            return null
        }
        // 查询条件
        var content = ""
        if (condition.isNotEmpty()) {
            content = condition.joinToString(prefix = "where ", separator = "and ")
        }
        // 设置排序方式
        val asc: String = if (ascFlag) {
            "asc"
        } else {
            "desc"
        }
        val cursor = database?.rawQuery("select * from $tableName $content order by $order $asc", null)
        val entities = convertToEntities(cursor)
        cursor?.close()
        return entities
    }

    /**
     * 获取数据总条数
     * @return 数据总条数
     */
    fun getCount(): Int {
        // 获取数据总条数并返回
        val cursor = database?.rawQuery("select COUNT(*) from $tableName", null)
        var count = 0
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0)
            cursor.close()
        }
        return count
    }
}