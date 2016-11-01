package com.xmx.tango.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoleilu.hutool.lang.Conver;
import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_import_file)
public class ImportFileFragment extends xUtilsFragment {
    static final int CHOOSE_FILE_RESULT = 1;

    boolean fileFlag = false;
    String filePath;

    @ViewInject(R.id.tv_file_path)
    TextView filePathView;

    @ViewInject(R.id.edit_type)
    EditText typeView;

    @Event(value = R.id.btn_choose_file)
    private void onChooseFileClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_FILE_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FILE_RESULT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            filePath = uri.getPath();
            filePathView.setText(filePath);
            fileFlag = true;
        }
    }

    @Event(value = R.id.btn_import_file)
    private void onImportFileClick(View view) {
        if (fileFlag) {
            if (filePath.contains("primary:")) {
                final String[] split = filePath.split(":");
                final String fileType = split[0];
                filePath = android.os.Environment
                        .getExternalStorageDirectory() + "/" + split[1];
            }
            parseCSV(filePath);
        } else {
            showToast("未选择文件");
        }
    }

    private void parseCSV(String filePath) {
        try {
            InputStream is = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String str = reader.readLine();
                if (str != null) {
                    String[] strings = str.split(",");
                    if (strings.length > 1) {
                        makeTango(strings);
                    }
                } else {
                    break;
                }
            }
            showToast("导入成功");
            is.close();
            EventBus.getDefault().post(new TangoListChangeEvent());
        } catch (Exception e) {
            filterException(e);
        }
    }

    private Tango makeTango(String[] strings) {
        String type = typeView.getText().toString().trim();

        Tango tango = new Tango();
        try {
            tango.writing = strings[0];
            tango.pronunciation = strings[1];
            tango.meaning = strings[2];
            tango.tone = Conver.toInt(strings[3], -1);
            tango.partOfSpeech = strings[4];
            tango.image = strings[5];
            tango.voice = strings[6];
            tango.score = Conver.toInt(strings[7], 0);
            tango.frequency = Conver.toInt(strings[8], 0);
            tango.addTime = new Date(Conver.toLong(strings[9], 0L));
            tango.lastTime = new Date(Conver.toLong(strings[10], 0L));
            tango.flags = strings[11];
            tango.delFlag = Conver.toInt(strings[12], 0);
        } catch (IndexOutOfBoundsException e) {
        } finally {
            tango.type = type;
            if (tango.addTime.getTime() == 0) {
                tango.addTime = new Date();
            }
            TangoEntityManager.getInstance().insertData(tango);
        }

        return tango;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
