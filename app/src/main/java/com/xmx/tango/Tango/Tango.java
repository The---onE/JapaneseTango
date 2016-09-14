package com.xmx.tango.Tango;

import android.content.ContentValues;
import android.database.Cursor;

import com.xmx.tango.Tools.Data.SQL.ISQLEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/9/13.
 */
public class Tango implements ISQLEntity {
    public long id = -1;
    public String writing;
    public String pronunciation;
    public String meaning;
    public String partOfSpeech;
    public String image;
    public String voice;
    public int score;
    public int frequency;
    public Date addTime;
    public Date lastTime;
    public String flags;

    @Override
    public String tableFields() {
        return "ID integer not null primary key autoincrement, " +
                "Writing text, " +
                "Pronunciation text, " +
                "Meaning text, " +
                "PartOfSpeech text, " +
                "Image text, " +
                "Voice text, " +
                "Score integer not null default(0), " +
                "Frequency integer not null default(0), " +
                "AddTime integer not null default(0), " +
                "LastTime integer not null default(0), " +
                "Flags text";
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
        content.put("PartOfSpeech", partOfSpeech);
        content.put("Image", image);
        content.put("Voice", voice);
        content.put("Score", score);
        content.put("Frequency", frequency);
        content.put("AddTime", addTime != null ? addTime.getTime() : 0);
        content.put("LastTime", lastTime != null ? lastTime.getTime() : 0);
        content.put("Flags", flags);
        return content;
    }

    @Override
    public Tango convertToEntity(Cursor c) {
        Tango entity = new Tango();
        entity.id = c.getLong(0);
        entity.writing = c.getString(1);
        entity.pronunciation = c.getString(2);
        entity.meaning = c.getString(3);
        entity.partOfSpeech = c.getString(4);
        entity.image = c.getString(5);
        entity.voice = c.getString(6);
        entity.score = c.getInt(7);
        entity.frequency = c.getInt(8);
        entity.addTime = new Date(c.getLong(9));
        entity.lastTime = new Date(c.getLong(10));
        entity.flags = c.getString(11);

        return entity;
    }
}
