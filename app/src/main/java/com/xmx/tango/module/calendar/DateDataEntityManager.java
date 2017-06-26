package com.xmx.tango.module.calendar;

import android.database.Cursor;

import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.common.data.sql.BaseSQLEntityManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2016/9/13.
 */
public class DateDataEntityManager extends BaseSQLEntityManager<DateData> {
    private static DateDataEntityManager instance;

    public synchronized static DateDataEntityManager getInstance() {
        if (null == instance) {
            instance = new DateDataEntityManager();
        }
        return instance;
    }

    private DateDataEntityManager() {
        tableName = "DateData";
        entityTemplate = new DateData();
        openDatabase();
    }
}
