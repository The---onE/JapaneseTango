package com.xmx.tango.module.calendar;

import android.content.ContentValues;
import android.database.Cursor;

import com.xmx.tango.common.data.sql.ISQLEntity;

import java.util.Date;

/**
 * Created by The_onE on 2016/9/13.
 */
public class DateData implements ISQLEntity {
    public long id = -1;
    public int year;
    public int month;
    public int date;
    public int checkIn;
    public int study;
    public int review;
    public int mission;
    public Date addTime = new Date(0);
    public String flags = "";
    public int delFlag = 0;

    @Override
    public String tableFields() {
        return "ID integer not null primary key autoincrement, " +
                "Year integer not null, " +
                "Month integer not null, " +
                "Date integer not null, " +
                "CheckIn integer not null default(-1), " +
                "Study integer not null default(0), " +
                "Review integer not null default(0), " +
                "Mission integer not null default(0), " +
                "AddTime integer not null default(0), " +
                "Flags text, " +
                "DelFlag integer not null default(0)";
    }

    @Override
    public ContentValues getContent() {
        ContentValues content = new ContentValues();
        if (id > 0) {
            content.put("ID", id);
        }
        content.put("Year", year);
        content.put("Month", month);
        content.put("Date", date);
        content.put("CheckIn", checkIn);
        content.put("Study", study);
        content.put("Review", review);
        content.put("Mission", mission);
        content.put("AddTime", addTime != null ? addTime.getTime() : 0);
        content.put("Flags", flags);
        content.put("DelFlag", delFlag);
        return content;
    }

    @Override
    public DateData convertToEntity(Cursor c) {
        DateData entity = new DateData();
        entity.id = c.getLong(0);
        entity.year = c.getInt(1);
        entity.month = c.getInt(2);
        entity.date = c.getInt(3);
        entity.checkIn = c.getInt(4);
        entity.study = c.getInt(5);
        entity.review = c.getInt(6);
        entity.mission = c.getInt(7);
        entity.addTime = new Date(c.getLong(8));
        entity.flags = c.getString(9);
        entity.delFlag = c.getInt(10);

        return entity;
    }
}
