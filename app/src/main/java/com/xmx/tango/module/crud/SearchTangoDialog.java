package com.xmx.tango.module.crud;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.base.dialog.BaseDialog;
import com.xmx.tango.module.tango.TangoManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by The_onE on 2016/9/21.
 */
public class SearchTangoDialog extends BaseDialog {

    EditText writingView;
    EditText pronunciationView;
    EditText meaningView;
    EditText partOfSpeechView;
    EditText typeView;

    private void orderTango() {
        String[] items = new String[]{"ID", "分数", "添加时间", "上次时间"};
        final String[] orders = new String[]{"ID", "Score", "AddTime", "LastTime"};
        new AlertDialog.Builder(mContext)
                .setTitle("操作")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TangoManager.getInstance().order.equals(orders[i])) {
                            TangoManager.getInstance().ascFlag = !TangoManager.getInstance().ascFlag;
                        } else {
                            TangoManager.getInstance().ascFlag = true;
                            TangoManager.getInstance().order = orders[i];
                        }

                        EventBus.getDefault().post(new TangoListChangeEvent());
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_search_tango, container);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        writingView = (EditText) view.findViewById(R.id.edit_writing);
        pronunciationView = (EditText) view.findViewById(R.id.edit_pronunciation);
        meaningView = (EditText) view.findViewById(R.id.edit_meaning);
        partOfSpeechView = (EditText) view.findViewById(R.id.edit_part_of_speech);
        typeView = (EditText) view.findViewById(R.id.edit_type);

        writingView.setText(TangoManager.getInstance().writing);
        pronunciationView.setText(TangoManager.getInstance().pronunciation);
        meaningView.setText(TangoManager.getInstance().meaning);
        partOfSpeechView.setText(TangoManager.getInstance().partOfSpeech);
        typeView.setText(TangoManager.getInstance().type);
    }

    @Override
    protected void setListener(View view) {
        view.findViewById(R.id.btn_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderTango();
            }
        });

        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                String pronunciation = pronunciationView.getText().toString();
                String meaning = meaningView.getText().toString();
                String partOfSpeech = partOfSpeechView.getText().toString();
                String type = typeView.getText().toString();

                TangoManager.getInstance().writing = writing;
                TangoManager.getInstance().pronunciation = pronunciation;
                TangoManager.getInstance().meaning = meaning;
                TangoManager.getInstance().partOfSpeech = partOfSpeech;
                TangoManager.getInstance().type = type;

                EventBus.getDefault().post(new TangoListChangeEvent());
                dismiss();
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {

    }
}
