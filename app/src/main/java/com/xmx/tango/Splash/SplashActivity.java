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
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.User.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class SplashActivity extends BaseActivity {

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
        Date last = new Date(DataManager.getInstance().getLong("last_time", 0));
        Date now = new Date();
        if (!isSameDate(now, last)) {
            DataManager.getInstance().setInt("tango_count", 0);
            DataManager.getInstance().setInt("tango_review", 0);
            DataManager.getInstance().setInt("review_frequency", Constants.REVIEW_FREQUENCY);
            DataManager.getInstance().setLong("last_time", now.getTime());
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        TangoManager.getInstance().updateData();
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

    void startLoginActivity() {
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    void startMainActivity() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    boolean isSameDate(Date now, Date last) {
        return now.getTime() - last.getTime() < Constants.DAY_TIME
                && now.getDate() == last.getDate();
    }
}