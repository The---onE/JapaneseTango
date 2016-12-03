package com.xmx.tango.Tango;

import android.database.Cursor;

import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.Tools.Data.SQL.BaseSQLEntityManager;
import com.xmx.tango.Tools.Utils.StrUtil;

import java.util.ArrayList;
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
        List<String> conditions = new ArrayList<>();

        String type = DataManager.getInstance().getTangoType();
        if (!type.equals("")) {
            conditions.add("Type = '" + type + "'");
        }

        if (maxFrequency >= 0) {
            conditions.add("Frequency <= " + maxFrequency);
        }

        String reviewStr;
        if (reviewFlag) {
            reviewStr = "Frequency desc, ";
        } else {
            reviewStr = "";
            conditions.add("Frequency >= 0");
        }

        String conStr = "";
        if (conditions.size() > 0) {
            conStr = " where ";
            conStr += StrUtil.join(conditions, " and ");
        }

        Cursor cursor = database.rawQuery("select * from " + tableName +
                        conStr +
                        " order by " + reviewStr + "Score asc, LastTime asc limit " + count,
                null);
        return convertToEntities(cursor);
    }
}
