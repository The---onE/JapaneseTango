package com.xmx.tango.Tango;

import android.database.Cursor;

import com.xmx.tango.Tools.Data.DataManager;
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

    public List<Tango> selectTangoScoreAsc(int count, boolean reviewFlag) {
        if (!checkDatabase()) {
            return null;
        }
        String type = DataManager.getInstance().getString("tango_type");
        String typeStr;
        if (!type.equals("")) {
            typeStr = " where Type = '" + type + "'";
        } else {
            typeStr = "";
        }

        String reviewStr;
        if (reviewFlag) {
            reviewStr = "Frequency desc, ";
        } else {
            reviewStr = "";
        }

        Cursor cursor = database.rawQuery("select * from " + tableName +
                        typeStr +
                        " order by " + reviewStr + "Score asc, LastTime asc limit " + count,
                null);
        return convertToEntities(cursor);
    }
}
