package com.xmx.tango.Tango;

import android.content.Intent;
import android.os.AsyncTask;

import com.xiaoleilu.hutool.lang.Conver;
import com.xmx.tango.MainActivity;
import com.xmx.tango.Tools.ServiceBase.BaseService;
import com.xmx.tango.Tools.Timer;
import com.xmx.tango.Tools.Utils.StrUtil;

import org.greenrobot.eventbus.EventBus;

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
                int total = tangoStrings.size();
                int index = 0;
                for (String str: tangoStrings) {
                    index++;
                    String[] strings = str.split(",");
                    Tango tango = makeTango(strings, type);
                    TangoEntityManager.getInstance().insertData(tango);
                    showForeground(MainActivity.class, "正在导入:" + index + "/" + total);
                }
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
