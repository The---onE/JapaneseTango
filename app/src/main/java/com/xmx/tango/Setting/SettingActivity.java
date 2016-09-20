package com.xmx.tango.Setting;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xmx.tango.Constants;
import com.xmx.tango.R;
import com.xmx.tango.Tango.SpeakTangoManager;
import com.xmx.tango.Tools.ActivityBase.BaseTempActivity;
import com.xmx.tango.Tools.Data.DataManager;

/**
 * Created by The_onE on 2016/9/17.
 */
public class SettingActivity extends BaseTempActivity {

    TextView typeView;
    TextView goalView;
    TextView speakView;
    EditText testView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);

        typeView = getViewById(R.id.tv_type);
        String type = DataManager.getInstance().getString("tango_type", "");
        if (type.equals("")) {
            type = "全部";
        }
        typeView.setText(type);

        goalView = getViewById(R.id.tv_goal);
        goalView.setText("" + DataManager.getInstance().getInt("tango_goal", 0));

        speakView = getViewById(R.id.tv_speaker);
        speakView.setText(DataManager.getInstance()
                .getString("tango_speaker", Constants.SPEAKERS[0]));

        testView = getViewById(R.id.edit_speak);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.layout_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText typeEdit = new EditText(getBaseContext());
                typeEdit.setTextColor(Color.BLACK);
                typeEdit.setTextSize(24);
                typeEdit.setText(DataManager.getInstance().getString("tango_type", ""));
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("学习的単語类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(typeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String type = typeEdit.getText().toString().trim();
                                DataManager.getInstance().setString("tango_type", type);
                                showToast("更改成功");
                                if (type.equals("")) {
                                    type = "全部";
                                }
                                typeView.setText(type);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText goalEdit = new EditText(getBaseContext());
                goalEdit.setTextColor(Color.BLACK);
                goalEdit.setTextSize(24);
                goalEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                goalEdit.setText("" + DataManager.getInstance().getInt("tango_goal", 0));
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("每日的学习目标")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(goalEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String goalString = goalEdit.getText().toString();
                                int goal = 0;
                                if (!goalString.equals("")) {
                                    goal = Integer.parseInt(goalString);
                                }
                                DataManager.getInstance().setInt("tango_goal", goal);
                                showToast("更改成功");
                                goalView.setText("" + goal);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_speaker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("朗读音色")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(Constants.SPEAKERS, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataManager.getInstance().setString("tango_speaker"
                                        , Constants.SPEAKERS[i]);
                                speakView.setText(Constants.SPEAKERS[i]);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.btn_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = testView.getText().toString();
                SpeakTangoManager.getInstance().speak(text);
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
