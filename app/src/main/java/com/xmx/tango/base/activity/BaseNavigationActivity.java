package com.xmx.tango.base.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.xmx.tango.R;
import com.xmx.tango.common.user.UserData;
import com.xmx.tango.common.user.callback.AutoLoginCallback;
import com.xmx.tango.common.user.callback.LogoutCallback;
import com.xmx.tango.common.user.UserConstants;
import com.xmx.tango.common.user.UserManager;
import com.xmx.tango.module.user.LoginActivity;

/**
 * Created by The_onE on 2015/12/28.
 */
public abstract class BaseNavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDrawerNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void initDrawerNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_setting:
                break;
            case R.id.nav_logout:
                final Intent intent = new Intent(BaseNavigationActivity.this, LoginActivity.class);
                if (UserManager.getInstance().isLoggedIn()) {
                    AlertDialog.Builder builder = new AlertDialog
                            .Builder(BaseNavigationActivity.this);
                    builder.setMessage("确定要注销吗？");
                    builder.setTitle("提示");
                    builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserManager.getInstance().logout(new LogoutCallback() {
                                @Override
                                public void logout(UserData user) {
                                    //SyncEntityManager.getInstance().getSQLManager().clearDatabase();
                                }
                            });
                            NavigationView navigation = getViewById(R.id.nav_view);
                            Menu menu = navigation.getMenu();
                            MenuItem login = menu.findItem(R.id.nav_logout);
                            login.setTitle("登录");
                            startActivityForResult(intent, LOGIN_REQUEST);
                        }
                    });
                    builder.show();
                } else {
                    startActivityForResult(intent, LOGIN_REQUEST);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            NavigationView navigation = getViewById(R.id.nav_view);
            Menu menu = navigation.getMenu();
            final MenuItem login = menu.findItem(R.id.nav_logout);
            UserManager.getInstance().autoLogin(new AutoLoginCallback() {
                @Override
                public void success(final UserData user) {
                    login.setTitle(user.nickname + " 点击注销");
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
    }
}