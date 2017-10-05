package com.xmx.tango.base.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xmx.tango.core.Application;
import com.xmx.tango.core.Constants;
import com.xmx.tango.common.log.OperationLogEntityManager;
import com.xmx.tango.utils.ExceptionUtil;

import org.xutils.x;

/**
 * Created by The_onE on 2015/12/27.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);

        TAG = this.getClass().getSimpleName();
        Application.getInstance().addActivity(this);

        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
    }

    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void setListener();

    protected abstract void processLogic(Bundle savedInstanceState);

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewCreated();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewCreated();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        onViewCreated();
    }

    protected void onViewCreated() {
    }

    protected void filterException(Exception e) {
        ExceptionUtil.INSTANCE.normalException(e, this);
    }

    protected void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    protected void showLog(String tag, String msg) {
        Log.e(tag, msg);
    }

    protected void showLog(String tag, int i) {
        Log.e(tag, "" + i);
    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void startActivity(Class<?> cls, String... objs) {
        Intent intent = new Intent(this, cls);
        for (int i = 0; i < objs.length; i++) {
            intent.putExtra(objs[i], objs[++i]);
        }
        startActivity(intent);
    }


    /**
     * 检查系统是否授予权限
     * @param permission 需要的权限 Manifest.permission.权限名
     * @param requestId 请求ID
     */
    public boolean checkLocalPhonePermission(String permission, int requestId) {
        if (Build.VERSION.SDK_INT >= 23) {
            // 是否已授权
            int permissionFlag = ActivityCompat.checkSelfPermission(this, permission);
            if (permissionFlag != PackageManager.PERMISSION_GRANTED) {
                // 若未授权则请求授权
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestId);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查定制系统(小米)是否授予权限
     * @param opsPermission 定制系统权限名 AppOpsManager.权限名
     * @param permission 需要的权限 Manifest.permission.权限名
     * @param requestId 请求ID
     */
    public boolean checkOpsPermission(String opsPermission, String permission, int requestId) {
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            // 是否已授权
            int permissionFlag = appOpsManager
                    .checkOp(opsPermission, Binder.getCallingUid(), getPackageName());
            if (permissionFlag != AppOpsManager.MODE_ALLOWED) {
                // 若未授权则请求授权
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestId);
                return false;
            }
        }
        return true;
    }
}
