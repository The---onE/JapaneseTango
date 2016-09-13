package com.xmx.tango.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xmx.tango.Constants;
import com.xmx.tango.MainActivity;
import com.xmx.tango.R;
import com.xmx.tango.Tools.ActivityBase.BaseActivity;
import com.xmx.tango.User.LoginActivity;

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
}