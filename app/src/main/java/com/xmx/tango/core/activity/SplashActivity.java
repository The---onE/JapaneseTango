package com.xmx.tango.core.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.xmx.tango.common.user.IUserManager;
import com.xmx.tango.common.user.LoginEvent;
import com.xmx.tango.common.user.UserConstants;
import com.xmx.tango.common.user.UserData;
import com.xmx.tango.common.user.UserManager;
import com.xmx.tango.common.user.callback.AutoLoginCallback;
import com.xmx.tango.core.Constants;
import com.xmx.tango.R;
import com.xmx.tango.module.calendar.DateData;
import com.xmx.tango.module.calendar.DateDataEntityManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.crud.TangoListChangeEvent;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.base.activity.BaseSplashActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.utils.ExceptionUtil;
import com.xmx.tango.utils.Timer;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class SplashActivity extends BaseSplashActivity {

    Timer timer;
    boolean readyFlag = false; // 数据库是否已更新完毕
    boolean timeFlag = false; // 是否已过自动跳转时间
    boolean skipFlag = false; // 是否已跳转

    Button btnSkip;

    private IUserManager userManager = UserManager.getInstance();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        btnSkip = getViewById(R.id.btn_skip);
    }

    @Override
    protected void setListener() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        timer = new Timer() {
            @Override
            public void timer() {
                timeFlag = true;
                skip();
            }
        };
        timer.start(Constants.SPLASH_TIME, true);

        TangoManager.getInstance().updateTangoList();
        Date last = new Date(DataManager.getInstance().getForgetLastTime());
        Date now = new Date();
        if (!Constants.isSameDate(now, last)) {
            if (last.getTime() > 0) {
                // 更新上次签到日数据
                DateData dateData = DateDataEntityManager.getInstance()
                        .selectLatest("addTime", false,
                                "Year=" + (last.getYear() + 1900),
                                "Month=" + (last.getMonth() + 1),
                                "Date=" + last.getDate());
                if (dateData != null) {
                    DateDataEntityManager.getInstance().updateData(dateData.id,
                            "Mission=" + DataManager.getInstance().getTodayMission());
                } else {
                    dateData = new DateData();
                    dateData.year = last.getYear() + 1900;
                    dateData.month = last.getMonth() + 1;
                    dateData.date = last.getDate();
                    dateData.checkIn = 1;
                    dateData.mission = DataManager.getInstance().getTodayMission();
                    dateData.addTime = now;
                    DateDataEntityManager.getInstance().insertData(dateData);
                }
            }
            // 今天打卡签到
            DateData todayData = DateDataEntityManager.getInstance()
                    .selectLatest("addTime", false,
                            "Year=" + (now.getYear() + 1900),
                            "Month=" + (now.getMonth() + 1),
                            "Date=" + now.getDate());
            if (todayData == null) {
                todayData = new DateData();
                todayData.year = now.getYear() + 1900;
                todayData.month = now.getMonth() + 1;
                todayData.date = now.getDate();
                todayData.checkIn = 1;
                todayData.addTime = now;
                DateDataEntityManager.getInstance().insertData(todayData);
            }

            DataManager.getInstance().setForgetLastTime(now.getTime());
            DataManager.getInstance().setTodayMission(0);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        List<Tango> tangoList = TangoManager.getInstance().getTangoList();
                        for (int i = 0; i < tangoList.size(); i++) {
                            Tango tango = tangoList.get(i);
                            if (tango.score > 0) {
                                int newScore = TangoConstants.FORGOTTEN_SCORE(tango.score);
                                if (newScore <= 0) {
                                    newScore = 1;
                                }
                                TangoEntityManager.getInstance().updateData(tango.id,
                                        "Score=" + newScore);
                            }
                        }
                    } catch (Exception e) {
                        filterException(e);
                    } finally {
                        ready();
                        if (timeFlag) {
                            skip();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    EventBus.getDefault().post(new TangoListChangeEvent());
                }
            }.execute();
        } else {
            ready();
        }
        // 使用设备保存的数据自动登录
        userManager.autoLogin(new AutoLoginCallback() {
            @Override
            public void success(final UserData user) {
                EventBus.getDefault().post(new LoginEvent());
            }

            @Override
            public void error(AVException e) {
                ExceptionUtil.normalException(e, getBaseContext());
            }

            @Override
            public void error(int error) {
                switch (error) {
                    case UserConstants.NOT_LOGGED_IN:
                        //showToast("请在侧边栏中选择登录");
                        break;
                    case UserConstants.USERNAME_ERROR:
                        showToast("请在侧边栏中选择登录");
                        break;
                    case UserConstants.CHECKSUM_ERROR:
                        showToast("登录过期，请在侧边栏中重新登录");
                        break;
                }
            }
        });
    }

    private void skip() {
        if (!skipFlag && readyFlag) {
            startMainActivity();
            skipFlag = true;
        }
    }

    private void ready() {
        readyFlag = true;
        btnSkip.setVisibility(View.VISIBLE);
    }
}