package com.xmx.tango;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.tango.Fragments.HomeFragment;
import com.xmx.tango.Fragments.TangoListFragment;
import com.xmx.tango.Log.OperationLogActivity;
import com.xmx.tango.Setting.SettingActivity;
import com.xmx.tango.Mission.MissionActivity;
import com.xmx.tango.Tools.ActivityBase.BaseNavigationActivity;
import com.xmx.tango.Tools.PagerAdapter;
import com.xmx.tango.User.Callback.AutoLoginCallback;
import com.xmx.tango.User.UserConstants;
import com.xmx.tango.User.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNavigationActivity {
    private long mExitTime = 0;

    ViewPager vp;

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

        vp = getViewById(R.id.pager_main);
        vp.setAdapter(adapter);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        NavigationView navigation = getViewById(R.id.nav_view);
        Menu menu = navigation.getMenu();
        final MenuItem login = menu.findItem(R.id.nav_logout);

        UserManager.getInstance().autoLogin(new AutoLoginCallback() {
            @Override
            public void success(final AVObject user) {
                login.setTitle(user.getString("nickname") + " 点击注销");
            }

            @Override
            public void error(AVException e) {
                filterException(e);
            }

            @Override
            public void error(int error) {
                switch (error) {
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
        } else if (id == R.id.action_mission) {
            startActivity(MissionActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
