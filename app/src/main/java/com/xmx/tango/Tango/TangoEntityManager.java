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

    public List<Tango> selectTangoScoreAsc(int count, boolean reviewFlag, int maxFrequency) {
        if (!checkDatabase()) {
            return null;
        }
        String type = DataManager.getInstance().getTangoType();
        String conStr;
        if (!type.equals("")) {
            conStr = " where Type = '" + type + "'";
        } else {
            conStr = "";
        }

        if (maxFrequency >= 0) {
            if (!conStr.equals("")) {
                conStr += " and Frequency <= " + maxFrequency;
            } else {
                conStr = " where Frequency <= " + maxFrequency;
            }
        }

        String reviewStr;
        if (reviewFlag) {
            reviewStr = "Frequency desc, ";
        } else {
            reviewStr = "";
            if (!conStr.equals("")) {
                conStr += " and Frequency >= 0";
            } else {
                conStr = " where Frequency >= 0";
            }
        }

        Cursor cursor = database.rawQuery("select * from " + tableName +
                        conStr +
                        " order by " + reviewStr + "Score asc, LastTime asc limit " + count,
                null);
        return convertToEntities(cursor);
    }
}
