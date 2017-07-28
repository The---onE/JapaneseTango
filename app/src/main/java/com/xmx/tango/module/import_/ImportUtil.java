package com.xmx.tango.module.import_;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.xmx.tango.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The_onE on 2017/7/28.
 */

public class ImportUtil {
    public static void showDialog(List<String> dialogStrings,
                                  final ArrayList<String> intentStrings,
                                  final String type,
                                  final Context context) {
        String array[] = new String[dialogStrings.size()];
        array = dialogStrings.toArray(array);
        new AlertDialog.Builder(context)
                .setTitle("识别出的単語")
                .setItems(array, null)
                .setPositiveButton("导入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StrUtil.showToast(context, "正在导入，请稍后");
//                            new NewThread() {
//                                @Override
//                                public void process() {
//                                    for (Tango t : tangoList) {
//                                        TangoEntityManager.getInstance().insertData(t);
//                                    }
//                                    showToast("导入成功");
//                                    EventBus.getDefault().post(new TangoListChangeEvent());
//                                }
//                            }.start();
                        Intent service = new Intent(context, ImportService.class);
                        service.putStringArrayListExtra("list", intentStrings);
                        service.putExtra("type", type);
                        context.startService(service);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
