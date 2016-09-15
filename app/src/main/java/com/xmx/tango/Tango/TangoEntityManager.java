package com.xmx.tango.Tango;

import android.database.Cursor;

import com.xmx.tango.Tools.Data.SQL.BaseSQLEntityManager;

import java.util.List;

/**
 * Created by The_onE on 2016/9/13.
 */
public class TangoEntityManager extends BaseSQLEntityManager<Tango> {
    private static TangoEntityManager instance;

    public synchronized static TangoEntityManager getInstance() {
        if (null == instance) {
            instance = new TangoEntityManager();
        }
        return instance;
    }

    private TangoEntityManager() {
        tableName = "Tango";
        entityTemplate = new Tango();
        openDatabase();
    }

    public List<Tango> selectTangoScoreAsc(int count) {
        if (!checkDatabase()) {
            return null;
        }
        Cursor cursor = database.rawQuery("select * from " + tableName +
                " order by Score asc, LastTime asc limit " + count,
                null);
        return convertToEntities(cursor);
    }
}
