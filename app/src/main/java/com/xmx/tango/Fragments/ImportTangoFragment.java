package com.xmx.tango.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;

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
@ContentView(R.layout.fragment_import_tango)
public class ImportTangoFragment extends xUtilsFragment {

    @ViewInject(R.id.edit_text)
    EditText textView;

    @ViewInject(R.id.edit_format)
    EditText formatView;

    @Event(value = R.id.btn_import_tango)
    private void onImportTangoClick(View view) {
        String text = textView.getText().toString();
        String format = formatView.getText().toString();
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(text);

        List<String> match = new ArrayList<>();
        final List<Tango> tangoList = new ArrayList<>();
        while (matcher.find()) {
            Tango t = new Tango();
            String writing = matcher.group(1);
            String pronunciation = matcher.group(2);

            t.writing = writing;
            t.pronunciation = pronunciation;
            match.add(writing + ":" + pronunciation);
            tangoList.add(t);
        }
        String array[] = new String[match.size()];
        array = match.toArray(array);
        new AlertDialog.Builder(getContext())
                .setTitle("列表框")
                .setItems(array, null)
                .setPositiveButton("导入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Tango t : tangoList) {
                            t.addTime = new Date();

                            TangoEntityManager.getInstance().insertData(t);
                        }
                        EventBus.getDefault().post(new TangoListChangeEvent());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
