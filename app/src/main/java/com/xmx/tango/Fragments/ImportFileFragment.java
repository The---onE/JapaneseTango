package com.xmx.tango.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
            String type = typeView.getText().toString().trim();
            try {
                InputStream is = new FileInputStream(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str = null;
                while (true) {
                    str = reader.readLine();
                    if (str != null) {
                        String[] strings = str.split(",");
                        if (strings.length > 1) {
                            Tango tango = new Tango();
                            if (strings.length > 0) {
                                tango.writing = strings[0];
                                if (strings.length > 1) {
                                    tango.pronunciation = strings[1];
                                    if (strings.length > 2) {
                                        tango.meaning = strings[2];
                                        if (strings.length > 3) {
                                            try {
                                                tango.tone = Integer.parseInt(strings[3]);
                                            } catch (Exception e) {
                                                tango.tone = -1;
                                            }
                                            if (strings.length > 4) {
                                                tango.partOfSpeech = strings[4];
                                            }
                                        }
                                    }
                                }
                            }

                            tango.type = type;

                            tango.addTime = new Date();
                            TangoEntityManager.getInstance().insertData(tango);
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
        } else {
            showToast("未选择文件");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
