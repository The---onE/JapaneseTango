package com.xmx.tango.module.import_;

import android.content.Intent;
import android.os.AsyncTask;

import com.xiaoleilu.hutool.lang.Conver;
import com.xmx.tango.core.activity.MainActivity;
import com.xmx.tango.common.data.sql.InsertCallback;
import com.xmx.tango.common.notification.NotificationUtils;
import com.xmx.tango.base.service.BaseService;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.crud.TangoListChangeEvent;
import com.xmx.tango.utils.StrUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportService extends BaseService {

    @Override
    protected void processLogic(Intent intent) {
        final List<String> tangoStrings = intent.getStringArrayListExtra("list");
        final String type = intent.getStringExtra("type");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final int total = tangoStrings.size();
                List<Tango> tangoList = new ArrayList<>();
                for (String str: tangoStrings) {
                    String[] strings = str.split(",");
                    Tango tango = makeTango(strings, type);
                    tangoList.add(tango);
                    //TangoEntityManager.getInstance().insertData(tango);
                    //showForeground(MainActivity.class, "正在导入:" + index + "/" + total);
                }
                TangoEntityManager.getInstance().insertData(tangoList, new InsertCallback() {
                    @Override
                    public void proceeding(int index) {
                        if (index % 100 == 0) {
                            showForeground(MainActivity.class, "正在导入:" + index + "/" + total);
                        }
                    }

                    @Override
                    public void success(int total) {
                        Intent intent = new Intent(ImportService.this, MainActivity.class);

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

    private Tango makeTango(String[] strings, String type) {
        Tango tango = new Tango();
        try {
            tango.writing = strings[0];
            tango.pronunciation = strings[1];
            tango.meaning = strings[2];
            tango.tone = Conver.toInt(strings[3], -1);
            tango.partOfSpeech = strings[4];
            tango.image = strings[5];
            tango.voice = strings[6];
            tango.score = Conver.toInt(strings[7], 0);
            tango.frequency = Conver.toInt(strings[8], 0);
            tango.addTime = new Date(Conver.toLong(strings[9], 0L));
            tango.lastTime = new Date(Conver.toLong(strings[10], 0L));
            tango.flags = strings[11];
            tango.delFlag = Conver.toInt(strings[12], 0);
            tango.type = strings[13];
        } catch (IndexOutOfBoundsException e) {
        } finally {
            if (!StrUtil.isBlank(type)) {
                tango.type = type;
            }
            if (tango.addTime.getTime() == 0) {
                tango.addTime = new Date();
            }
        }

        return tango;
    }
}
