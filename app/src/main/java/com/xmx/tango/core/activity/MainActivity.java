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

import com.xmx.tango.core.fragments.HomeFragment;
import com.xmx.tango.core.fragments.SentenceFragment;
import com.xmx.tango.core.fragments.TangoListFragment;
import com.xmx.tango.module.calendar.CalendarActivity;
import com.xmx.tango.module.log.OperationLogActivity;
import com.xmx.tango.R;
import com.xmx.tango.module.mission.MissionActivity;
import com.xmx.tango.base.activity.BaseNavigationActivity;
import com.xmx.tango.core.HomePagerAdapter;
import com.xmx.tango.core.CoreConstants;
import com.xmx.tango.module.crud.ChooseTangoEvent;
import com.xmx.tango.module.service.TangoService;
import com.xmx.tango.module.test.TestActivity;
import com.xmx.tango.module.test.TypewriterActivity;
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

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new TangoListFragment());
        fragments.add(new SentenceFragment());

        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("单词");
        titles.add("句子");

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), fragments, titles);

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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > CoreConstants.INSTANCE.getLONGEST_EXIT_TIME()) {
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
            case R.id.nav_sentence:
                vp.setCurrentItem(2);
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
        } else if (id == R.id.action_typewriter) {
            startActivity(TypewriterActivity.class);
            return true;
        } else if (id == R.id.action_mission) {
            startActivity(MissionActivity.class);
            return true;
        } else if (id == R.id.action_test) {
            startActivity(TestActivity.class);
            return true;
        } else if (id == R.id.action_check_in) {
            startActivity(CalendarActivity.class);
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
                    .getShortClassName().equals(".module.service.TangoService")) {
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
    public void onEvent(ChooseTangoEvent event) {
        vp.setCurrentItem(1);
    }
}
