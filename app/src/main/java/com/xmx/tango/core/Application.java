package com.xmx.tango.core;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.xmx.tango.BuildConfig;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.common.push.ReceiveMessageActivity;
import com.xmx.tango.common.user.UserManager;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by The_onE on 2016/1/3.
 */
public class Application extends MultiDexApplication {

    private static Application instance;
    public static Application getInstance() {
        return instance;
    }

    private List<Activity> activityList = new LinkedList<>();

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        AVOSCloud.initialize(this, Constants.APP_ID, Constants.APP_KEY);

        PushService.setDefaultPushCallback(this, ReceiveMessageActivity.class);
        PushService.subscribe(this, "system", ReceiveMessageActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();

        UserManager.getInstance().setContext(this);

        DataManager.getInstance().setContext(this);
    }
}
