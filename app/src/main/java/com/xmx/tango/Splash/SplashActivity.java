package com.xmx.tango.Splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.xmx.tango.Constants;
import com.xmx.tango.MainActivity;
import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tools.ActivityBase.BaseActivity;
import com.xmx.tango.Tools.ActivityBase.BaseSplashActivity;
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.User.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, Constants.SPLASH_TIME);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
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