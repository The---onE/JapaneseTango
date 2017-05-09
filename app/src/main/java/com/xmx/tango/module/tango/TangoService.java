package com.xmx.tango.module.tango;

import android.content.Intent;
import android.os.AsyncTask;

import com.xiaoleilu.hutool.lang.Conver;
import com.xmx.tango.base.service.BaseService;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.common.data.sql.InsertCallback;
import com.xmx.tango.common.notification.NotificationUtils;
import com.xmx.tango.core.Constants;
import com.xmx.tango.core.activity.MainActivity;
import com.xmx.tango.utils.StrUtil;
import com.xmx.tango.utils.Timer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TangoService extends BaseService {

    Timer timer;
    Tango tango;
    Tango prevTango;
    static final long DURATION = 5 * Constants.SECOND_TIME;

    @Override
    protected void processLogic(Intent intent) {
        timer = new Timer() {
            @Override
            public void timer() {
                loadNewTango();
            }
        };
        timer.start(DURATION);
    }

    @Override
    protected void setForeground(Intent intent) {
        loadNewTango();
    }

    private void loadNewTango() {
        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.getInstance().study >= goal;
        Tango temp = TangoManager.getInstance().randomTango(reviewFlag,
                DataManager.getInstance().getReviewFrequency(), tango);
        loadNewTango(temp);
    }

    private void loadNewTango(Tango newTango) {
        if (tango != null) {
            prevTango = tango;
        }

        tango = newTango;
        String word = tango.writing + "(" + tango.pronunciation + ")";
        String meaning = "[" + tango.partOfSpeech + "]" + tango.meaning;
        showForeground(MainActivity.class, word, meaning);
    }
}