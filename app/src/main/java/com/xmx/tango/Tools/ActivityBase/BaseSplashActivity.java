package com.xmx.tango.Tools.ActivityBase;

import android.content.Intent;

import com.xmx.tango.MainActivity;
import com.xmx.tango.Splash.SplashBannerActivity;
import com.xmx.tango.User.LoginActivity;

/**
 * Created by The_onE on 2016/10/8.
 */
public abstract class BaseSplashActivity extends BaseActivity {
    protected void startLoginActivity() {
        Intent loginIntent = new Intent(BaseSplashActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    protected void startMainActivity() {
        Intent mainIntent = new Intent(BaseSplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
