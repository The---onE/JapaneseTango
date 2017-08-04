package com.xmx.tango.module.tango;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.xmx.tango.common.data.sql.ISQLEntity;

import java.util.Date;
import java.util.Map;

/**
 * Created by The_onE on 2016/9/13.
 */
public class Tango implements ISQLEntity, Parcelable {
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

    public Tango() {

    }

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

    public static Tango convertFromJson(Map<String, Object> map) {
        Tango entity = new Tango();
        entity.id = (long) map.get("id");
        entity.writing = (String) map.get("writing");
        entity.pronunciation = (String) map.get("pronunciation");
        entity.meaning = (String) map.get("meaning");
        entity.tone = (int) map.get("tone");
        entity.partOfSpeech = (String) map.get("partOfSpeech");

        return entity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected Tango(Parcel in) {
        id = in.readLong();
        writing = in.readString();
        pronunciation = in.readString();
        meaning = in.readString();
        tone = in.readInt();
        partOfSpeech = in.readString();
        image = in.readString();
        voice = in.readString();
        score = in.readInt();
        frequency = in.readInt();
        flags = in.readString();
        delFlag = in.readInt();
        type = in.readString();
    }

    public static final Creator<Tango> CREATOR = new Creator<Tango>() {
        @Override
        public Tango createFromParcel(Parcel in) {
            return new Tango(in);
        }

        @Override
        public Tango[] newArray(int size) {
            return new Tango[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(writing);
        parcel.writeString(pronunciation);
        parcel.writeString(meaning);
        parcel.writeInt(tone);
        parcel.writeString(partOfSpeech);
        parcel.writeString(image);
        parcel.writeString(voice);
        parcel.writeInt(score);
        parcel.writeInt(frequency);
        parcel.writeString(flags);
        parcel.writeInt(delFlag);
        parcel.writeString(type);
    }
}
