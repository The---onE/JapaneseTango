package com.xmx.tango.common.log;

import com.xmx.tango.common.data.sql.BaseSQLEntityManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by The_onE on 2016/9/4.
 */
public class OperationLogEntityManager extends BaseSQLEntityManager<OperationLog> {
    private static OperationLogEntityManager instance;

    public synchronized static OperationLogEntityManager getInstance() {
        if (null == instance) {
            instance = new OperationLogEntityManager();
        }
        return instance;
    }

    private OperationLogEntityManager() {
        tableName = "OperationLog";
        entityTemplate = new OperationLog();
        openDatabase();
    }

    public void addLog(String operation) {
        OperationLog entity = new OperationLog();
        entity.mOperation = operation;
        entity.mTime = new Date();
        insertData(entity);
        EventBus.getDefault().post(new LogChangeEvent());
    }
}
