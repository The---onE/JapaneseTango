package com.xmx.tango.module.tango

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

import com.xmx.tango.common.data.sql.ISQLEntity

import java.util.Date

/**
 * Created by The_onE on 2016/9/13.
 * 单语实体
 */
class Tango : ISQLEntity, Parcelable {
    var id: Long = -1 // ID
    var writing = "" // 写法
    var pronunciation = "" // 发音
    var meaning = "" // 解释
    var tone = -1 // 音调
    var partOfSpeech = "" // 词性
    var image = "" // 图片
    var voice = "" // 朗读
    var score = 0 // 分数
    var frequency = 0 // 复习频率
    var addTime: Date? = Date(0) // 添加时间
    var lastTime: Date? = Date(0) // 上次学习时间
    var flags = "" // 备注
    var delFlag = 0 // 是否删除
    var type = "" // 类型

    constructor() {
    }

    private constructor(i: Parcel) {
        id = i.readLong()
        writing = i.readString()
        pronunciation = i.readString()
        meaning = i.readString()
        tone = i.readInt()
        partOfSpeech = i.readString()
        image = i.readString()
        voice = i.readString()
        score = i.readInt()
        frequency = i.readInt()
        flags = i.readString()
        delFlag = i.readInt()
        type = i.readString()
        addTime = Date(i.readLong())
        lastTime = Date(i.readLong())
    }

    override fun describeContents(): Int = 0

    companion object {
        fun convertFromJson(map: Map<String, Any>): Tango {
            val entity = Tango()
            entity.id = map["id"] as Long
            entity.writing = map["writing"] as String
            entity.pronunciation = map["pronunciation"] as String
            entity.meaning = map["meaning"] as String
            entity.tone = map["tone"] as Int
            entity.partOfSpeech = map["partOfSpeech"] as String

            return entity
        }

        val CREATOR: Parcelable.Creator<Tango> = object : Parcelable.Creator<Tango> {
            override fun createFromParcel(i: Parcel): Tango = Tango(i)
            override fun newArray(size: Int): Array<Tango?> = arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeLong(id)
        parcel.writeString(writing)
        parcel.writeString(pronunciation)
        parcel.writeString(meaning)
        parcel.writeInt(tone)
        parcel.writeString(partOfSpeech)
        parcel.writeString(image)
        parcel.writeString(voice)
        parcel.writeInt(score)
        parcel.writeInt(frequency)
        parcel.writeString(flags)
        parcel.writeInt(delFlag)
        parcel.writeString(type)
        parcel.writeLong(addTime!!.time)
        parcel.writeLong(lastTime!!.time)
    }

    override fun tableFields(): String {
        return "ID integer not null primary key autoincrement, " +
                "Writing text, " +
                "Pronunciation text, " +
                "Meaning text, " +
                "Tone integer not null default(-1), " +
                "PartOfSpeech text, " +
                "Image text, " +
                "Voice text, " +
                "Score integer not null default(0), " +
                "Frequency integer not null default(0), " +
                "AddTime integer not null default(0), " +
                "LastTime integer not null default(0), " +
                "Flags text, " +
                "DelFlag integer not null default(0), " +
                "Type text"
    }

    override fun getContent(): ContentValues {
        val content = ContentValues()
        if (id > 0) {
            content.put("ID", id)
        }
        content.put("Writing", writing)
        content.put("Pronunciation", pronunciation)
        content.put("Meaning", meaning)
        content.put("Tone", tone)
        content.put("PartOfSpeech", partOfSpeech)
        content.put("Image", image)
        content.put("Voice", voice)
        content.put("Score", score)
        content.put("Frequency", frequency)
        content.put("AddTime", addTime?.time ?: 0)
        content.put("LastTime", lastTime?.time ?: 0)
        content.put("Flags", flags)
        content.put("DelFlag", delFlag)
        content.put("Type", type)
        return content
    }

    override fun convertToEntity(c: Cursor): Tango {
        val entity = Tango()
        entity.id = c.getLong(0)
        entity.writing = c.getString(1)
        entity.pronunciation = c.getString(2)
        entity.meaning = c.getString(3)
        entity.tone = c.getInt(4)
        entity.partOfSpeech = c.getString(5)
        entity.image = c.getString(6)
        entity.voice = c.getString(7)
        entity.score = c.getInt(8)
        entity.frequency = c.getInt(9)
        entity.addTime = Date(c.getLong(10))
        entity.lastTime = Date(c.getLong(11))
        entity.flags = c.getString(12)
        entity.delFlag = c.getInt(13)
        entity.type = c.getString(14)

        return entity
    }
}
