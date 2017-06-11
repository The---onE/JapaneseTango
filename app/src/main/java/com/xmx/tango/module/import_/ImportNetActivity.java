package com.xmx.tango.module.import_;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.net.HttpGetCallback;
import com.xmx.tango.common.net.HttpManager;
import com.xmx.tango.module.net.NetConstants;
import com.xmx.tango.utils.ExceptionUtil;
import com.xmx.tango.utils.JSONUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_import_net)
public class ImportNetActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_type)
    EditText typeView;

    @Event(value = R.id.btn_import_net)
    private void onImportNetClick(final View view) {
        String type = typeView.getText().toString();
        Map<String, String> condition = new HashMap<>();
        if (type.trim().equals("")) {
            condition.put("type", type.trim());
        }
        view.setEnabled(false);
        HttpManager.getInstance().get(NetConstants.TANGO_LIST_URL, condition, new HttpGetCallback() {
            @Override
            public void success(String result) {
                view.setEnabled(true);
                result = result.trim();
                if (result.startsWith("{")) {
                    try {
                        Map<String, Object> map = JSONUtil.parseObject(result);
                        String status = (String) map.get(JSONUtil.RESPONSE_STATUS);
                        switch (status) {
                            case JSONUtil.STATUS_QUERY_SUCCESS:
                                showToast((String) map.get(JSONUtil.RESPONSE_PROMPT));
                                List<Object> entities = (List<Object>) map.get(JSONUtil.RESPONSE_ENTITIES);
                                List<String> dialogStrings = new ArrayList<>();
                                final ArrayList<String> intentStrings = new ArrayList<>();
                                for (Object item : entities) {
                                    String str = item.toString();
                                    String[] strings = str.split(",");
                                    if (strings.length >= 3) {
                                        intentStrings.add(str);
                                        dialogStrings.add(strings[0] + ":" + strings[1] + "|" + strings[2]);
                                    }
                                }
                                String array[] = new String[dialogStrings.size()];
                                array = dialogStrings.toArray(array);
                                new AlertDialog.Builder(ImportNetActivity.this)
                                        .setTitle("识别出的単語")
                                        .setItems(array, null)
                                        .setPositiveButton("导入", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                showToast("正在导入，请稍后");
                                                Intent service = new Intent(ImportNetActivity.this, ImportService.class);
                                                service.putStringArrayListExtra("list", intentStrings);
                                                String type = typeView.getText().toString().trim();
                                                service.putExtra("type", type);
                                                startService(service);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                                break;
                            case JSONUtil.STATUS_ERROR:
                                showToast((String) map.get(JSONUtil.RESPONSE_PROMPT));
                                break;
                            case JSONUtil.STATUS_EXECUTE_SUCCESS:
                                showToast((String) map.get(JSONUtil.RESPONSE_PROMPT));
                                break;
                        }
                    } catch (Exception e) {
                        ExceptionUtil.normalException(e, ImportNetActivity.this);
                        showToast("数据异常");
                    }
                } else {
                    showToast("服务器连接失败");
                }
            }

            @Override
            public void fail(Exception e) {
                showToast("服务器连接失败");
                view.setEnabled(true);
            }
        });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("网络导入");
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
