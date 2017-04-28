package com.xmx.tango.module.tango;

import android.content.ContentValues;
import android.database.Cursor;

import com.xmx.tango.common.data.sql.ISQLEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/9/13.
 */
public class Tango implements ISQLEntity {
    public long id = -1;
    public String writing = "";
    public String pronunciation = "";
    public String meaning = "";
    public int tone = -1;
    public String partOfSpeech = "";
    public String image = "";
    public String voice = "";
    public int score = 0;
    public int frequency = 0;
    public Date addTime = new Date(0);
    public Date lastTime = new Date(0);
    public String flags = "";
    public int delFlag = 0;
    public String type = "";

    @Override
    public String tableFields() {
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
                "Type text";
    }

    @Override
    public ContentValues getContent() {
        ContentValues content = new ContentValues();
        if (id > 0) {
            content.put("ID", id);
        }
        content.put("Writing", writing);
        content.put("Pronunciation", pronunciation);
        content.put("Meaning", meaning);
        content.put("Tone", tone);
        content.put("PartOfSpeech", partOfSpeech);
        content.put("Image", image);
        content.put("Voice", voice);
        content.put("Score", score);
        content.put("Frequency", frequency);
        content.put("AddTime", addTime != null ? addTime.getTime() : 0);
        content.put("LastTime", lastTime != null ? lastTime.getTime() : 0);
        content.put("Flags", flags);
        content.put("DelFlag", delFlag);
        content.put("Type", type);
        return content;
    }

    @Override
    public Tango convertToEntity(Cursor c) {
        Tango entity = new Tango();
        entity.id = c.getLong(0);
        entity.writing = c.getString(1);
        entity.pronunciation = c.getString(2);
        entity.meaning = c.getString(3);
        entity.tone = c.getInt(4);
        entity.partOfSpeech = c.getString(5);
        entity.image = c.getString(6);
        entity.voice = c.getString(7);
        entity.score = c.getInt(8);
        entity.frequency = c.getInt(9);
        entity.addTime = new Date(c.getLong(10));
        entity.lastTime = new Date(c.getLong(11));
        entity.flags = c.getString(12);
        entity.delFlag = c.getInt(13);
        entity.type = c.getString(14);

        return entity;
    }
}
