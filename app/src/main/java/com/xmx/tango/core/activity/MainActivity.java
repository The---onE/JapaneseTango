package com.xmx.tango.core.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.xmx.tango.common.user.IUserManager;
import com.xmx.tango.common.user.LoginEvent;
import com.xmx.tango.common.user.UserData;
import com.xmx.tango.core.fragments.HomeFragment;
import com.xmx.tango.core.fragments.TangoListFragment;
import com.xmx.tango.module.log.OperationLogActivity;
import com.xmx.tango.R;
import com.xmx.tango.module.mission.MissionActivity;
import com.xmx.tango.base.activity.BaseNavigationActivity;
import com.xmx.tango.core.PagerAdapter;
import com.xmx.tango.common.user.callback.AutoLoginCallback;
import com.xmx.tango.common.user.UserConstants;
import com.xmx.tango.common.user.UserManager;
import com.xmx.tango.core.Constants;
import com.xmx.tango.module.tango.ChooseTangoEvent;
import com.xmx.tango.module.tango.TangoService;
import com.xmx.tango.utils.ExceptionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNavigationActivity {
    private long mExitTime = 0;

    ViewPager vp;
    // 侧滑菜单登录菜单项
    MenuItem login;

    private IUserManager userManager = UserManager.getInstance();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new TangoListFragment());

        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("列表");

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments, titles);

        vp = getViewById(R.id.view_pager);
        vp.setAdapter(adapter);
        // 设置标签页底部选项卡
        TabLayout tabLayout = getViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(vp);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        NavigationView navigation = getViewById(R.id.nav_view);
        Menu menu = navigation.getMenu();
        login = menu.findItem(R.id.nav_logout);

        // 在SplashActivity中自动登录，在此校验登录
        if (userManager.isLoggedIn()) {
            checkLogin();
        }
    }

    // 处理登录返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserConstants.LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            // 登录成功
            checkLogin();
        }
    }

    private void checkLogin() {
        userManager.checkLogin(new AutoLoginCallback() {
            @Override
            public void success(UserData user) {
                login.setTitle(user.nickname + " 点击注销");
            }

            @Override
            public void error(int error) {
                switch (error) {
                    case UserConstants.CANNOT_CHECK_LOGIN:
                        showToast("请先登录");
                        break;
                    case UserConstants.NOT_LOGGED_IN:
                        showToast("请在侧边栏中选择登录");
                        break;
                    case UserConstants.USERNAME_ERROR:
                        showToast("请在侧边栏中选择登录");
                        break;
                    case UserConstants.CHECKSUM_ERROR:
                        showToast("登录过期，请在侧边栏中重新登录");
                        break;
                }
            }

            @Override
            public void error(AVException e) {
                ExceptionUtil.normalException(e, getBaseContext());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > Constants.LONGEST_EXIT_TIME) {
                showToast(R.string.confirm_exit);
                mExitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        super.onNavigationItemSelected(item);

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                vp.setCurrentItem(0);
                break;
            case R.id.nav_tango_list:
                vp.setCurrentItem(1);
                break;
            case R.id.nav_setting:
                startActivity(SettingActivity.class);
                break;
            case R.id.nav_log:
                startActivity(OperationLogActivity.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(SettingActivity.class);
            return true;
        } else if (id == R.id.action_switch_service) {
            switchService();
            return true;
        } else if (id == R.id.action_mission) {
            startActivity(MissionActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void switchService() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int defaultNum = 10000;
        List<ActivityManager.RunningServiceInfo> runServiceList = manager
                .getRunningServices(defaultNum);
        boolean flag = false;
        for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {
            if (runServiceInfo.service
                    .getShortClassName().equals(".module.tango.TangoService")) {
                Intent intent = new Intent();
                intent.setComponent(runServiceInfo.service);
                stopService(intent);
                showToast("已关闭服务");
                flag = true;
            }
        }
        if (!flag) {
            startService(new Intent(this, TangoService.class));
            showToast("已开启服务");
        }
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
        checkLogin();
    }

    @Subscribe
    public void onEvent(ChooseTangoEvent event) {
        vp.setCurrentItem(1);
    }
}
