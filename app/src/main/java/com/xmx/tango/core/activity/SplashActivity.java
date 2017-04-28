package com.xmx.tango.core.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.xmx.tango.core.Constants;
import com.xmx.tango.R;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.tango.TangoListChangeEvent;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.base.activity.BaseSplashActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.utils.Timer;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class SplashActivity extends BaseSplashActivity {

    Timer timer;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.stop();
                timer.execute();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        timer = new Timer() {
            @Override
            public void timer() {
                startMainActivity();
            }
        };
        timer.start(Constants.SPLASH_TIME, true);
        TangoManager.getInstance().updateData();
        Date last = new Date(DataManager.getInstance().getForgetLastTime());
        Date now = new Date();
        if (!isSameDate(now, last)) {
            DataManager.getInstance().setForgetLastTime(now.getTime());
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        List<Tango> tangoList = TangoManager.getInstance().getData();
                        for (int i = 0; i < tangoList.size(); i++) {
                            Tango tango = tangoList.get(i);
                            if (tango.score > 0) {
                                int newScore = Constants.FORGOTTEN_SCORE(tango.score);
                                if (newScore <= 0) {
                                    newScore = 1;
                                }
                                TangoEntityManager.getInstance().updateData(tango.id,
                                        "Score=" + newScore);
                            }
                        }
                        EventBus.getDefault().post(new TangoListChangeEvent());
                    } catch (Exception e) {
                        filterException(e);
                    }
                    return null;
                }
            }.execute();
        }
    }

    boolean isSameDate(Date now, Date last) {
        return now.getTime() - last.getTime() < Constants.DAY_TIME
                && now.getDate() == last.getDate();
    }
}