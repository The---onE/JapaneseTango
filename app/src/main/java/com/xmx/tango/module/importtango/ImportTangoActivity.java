package com.xmx.tango.module.importtango;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.tango.TangoListChangeEvent;
import com.xmx.tango.base.activity.BaseTempActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_import_tango)
public class ImportTangoActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_text)
    EditText textView;

    @ViewInject(R.id.edit_format)
    EditText formatView;

    @ViewInject(R.id.edit_writing_index)
    EditText writingIndexView;

    @ViewInject(R.id.edit_pronunciation_index)
    EditText pronunciationIndexView;

    @ViewInject(R.id.edit_meaning_index)
    EditText meaningIndexView;

    @ViewInject(R.id.edit_type)
    EditText typeView;

    @Event(value = R.id.btn_import_tango)
    private void onImportTangoClick(View view) {
        String text = textView.getText().toString();
        String format = formatView.getText().toString();
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(text);

        String writingIndexStr = writingIndexView.getText().toString();
        int writingIndex = -1;
        if (!writingIndexStr.equals("")) {
            writingIndex = Integer.parseInt(writingIndexStr);
        }

        String pronunciationIndexStr = pronunciationIndexView.getText().toString();
        int pronunciationIndex = -1;
        if (!pronunciationIndexStr.equals("")) {
            pronunciationIndex = Integer.parseInt(pronunciationIndexStr);
        }

        String meaningIndexStr = meaningIndexView.getText().toString();
        int meaningIndex = -1;
        if (!meaningIndexStr.equals("")) {
            meaningIndex = Integer.parseInt(meaningIndexStr);
        }

        final String type = typeView.getText().toString().trim();

        List<String> match = new ArrayList<>();
        final List<Tango> tangoList = new ArrayList<>();
        while (matcher.find()) {
            Tango t = new Tango();
            String writing = "";
            if (writingIndex > 0) {
                writing = matcher.group(writingIndex);
                writing = writing.trim();
            }
            String pronunciation = "";
            if (pronunciationIndex > 0) {
                pronunciation = matcher.group(pronunciationIndex);
                pronunciation = pronunciation.trim();
            }
            String meaning = "";
            if (meaningIndex > 0) {
                meaning = matcher.group(meaningIndex);
                meaning = meaning.trim();
            }

            t.writing = writing;
            t.pronunciation = pronunciation;
            t.meaning = meaning;
            match.add(writing + ":" + pronunciation + "|" + meaning);

            t.type = type;
            t.addTime = new Date();

            tangoList.add(t);
        }
        String array[] = new String[match.size()];
        array = match.toArray(array);
        new AlertDialog.Builder(ImportTangoActivity.this)
                .setTitle("识别出的単語")
                .setItems(array, null)
                .setPositiveButton("导入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TangoEntityManager.getInstance().insertData(tangoList);
                        EventBus.getDefault().post(new TangoListChangeEvent());
                        showToast("导入成功");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("文本导入");
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
