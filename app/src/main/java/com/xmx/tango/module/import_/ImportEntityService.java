package com.xmx.tango.module.import_;

import android.content.Intent;
import android.os.AsyncTask;

import com.xmx.tango.base.service.BaseService;
import com.xmx.tango.common.data.sql.InsertCallback;
import com.xmx.tango.common.notification.NotificationUtils;
import com.xmx.tango.core.activity.MainActivity;
import com.xmx.tango.module.crud.TangoListChangeEvent;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.utils.StrUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportEntityService extends BaseService {

    @Override
    protected void processLogic(Intent intent) {
        final List<Tango> tangoList = intent.getParcelableArrayListExtra("list");
        final String type = intent.getStringExtra("type");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final int total = tangoList.size();
                TangoEntityManager.INSTANCE.insertData(tangoList, new InsertCallback() {
                    @Override
                    public void proceeding(int index) {
                        if (index % 100 == 0) {
                            showForeground(MainActivity.class, "正在导入:" + index + "/" + total);
                        }
                    }

                    @Override
                    public void success(int total) {
                        Intent intent = new Intent(ImportEntityService.this, MainActivity.class);

                        NotificationUtils.showNotification(getBaseContext(), intent, 0, "日词",
                                "成功导入 " + total + " 条数据");
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showToast("导入成功");
                EventBus.getDefault().post(new TangoListChangeEvent());
                stopSelf();
            }
        }.execute();
    }

    @Override
    protected void setForeground(Intent intent) {
        showForeground(MainActivity.class, "正在导入");
    }
}
