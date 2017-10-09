package com.xmx.tango.module.import_;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.xiaoleilu.hutool.lang.Conver;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.utils.StrUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by The_onE on 2017/7/28.
 */

public class ImportUtil {
    public static void showDialog(List<String> dialogStrings,
                                  final ArrayList<String> intentStrings,
//                                  final ArrayList<Tango> tangoList,
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
                        StrUtil.INSTANCE.showToast(context, "正在导入，请稍后");
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
//                        Intent service = new Intent(context, ImportService.class);
//                        service.putStringArrayListExtra("list", intentStrings);
                        Intent service = new Intent(context, ImportEntityService.class);
                        ArrayList<Tango> tangoList = new ArrayList<>();
                        for (String str : intentStrings) {
                            String[] strings = str.split(",");
                            tangoList.add(makeTango(strings, type));
                        }
                        service.putParcelableArrayListExtra("list", tangoList);
                        service.putExtra("type", type);
                        context.startService(service);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static Tango makeTango(String[] strings, String type) {
        Tango tango = new Tango();
        try {
            tango.setWriting(strings[0]);
            tango.setPronunciation(strings[1]);
            tango.setMeaning(strings[2]);
            tango.setTone(Conver.toInt(strings[3], -1));
            tango.setPartOfSpeech(strings[4]);
            tango.setImage(strings[5]);
            tango.setVoice(strings[6]);
            tango.setScore(Conver.toInt(strings[7], 0));
            tango.setFrequency(Conver.toInt(strings[8], 0));
            tango.setAddTime(new Date(Conver.toLong(strings[9], 0L)));
            tango.setLastTime(new Date(Conver.toLong(strings[10], 0L)));
            tango.setFlags(strings[11]);
            tango.setDelFlag(Conver.toInt(strings[12], 0));
            tango.setType(strings[13]);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            if (!StrUtil.INSTANCE.isBlank(type)) {
                tango.setType(type);
            }
            if (tango.getAddTime().getTime() == 0) {
                tango.setAddTime(new Date());
            }
        }

        return tango;
    }
}
