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
import com.xmx.tango.Tango.LoadNewTangoEvent;
import com.xmx.tango.Tango.SpeakTangoManager;
import com.xmx.tango.Tools.ActivityBase.BaseTempActivity;
import com.xmx.tango.Tools.Data.DataManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by The_onE on 2016/9/17.
 */
public class SettingActivity extends BaseTempActivity {

    TextView typeView;
    TextView partView;
    TextView goalView;
    TextView pronunciationTimeView;
    TextView writingTimeView;
    TextView meaningTimeView;
    TextView frequencyView;
    TextView speakView;
    EditText testView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);

        typeView = getViewById(R.id.tv_type);
        String type = DataManager.getInstance().getTangoType();
        if (type.equals("")) {
            type = "全部";
        }
        typeView.setText(type);

        partView = getViewById(R.id.tv_part);
        String part = DataManager.getInstance().getPartOfSpeech();
        if (part.equals("")) {
            part = "全部";
        }
        partView.setText(part);

        goalView = getViewById(R.id.tv_goal);
        goalView.setText("" + DataManager.getInstance().getTangoGoal());

        pronunciationTimeView = getViewById(R.id.tv_pronunciation_time);
        pronunciationTimeView.setText("" + DataManager.getInstance().getPronunciationTime());

        writingTimeView = getViewById(R.id.tv_writing_time);
        writingTimeView.setText("" + DataManager.getInstance().getWritingTime());

        meaningTimeView = getViewById(R.id.tv_meaning_time);
        meaningTimeView.setText("" + DataManager.getInstance().getMeaningTime());

        frequencyView = getViewById(R.id.tv_frequency);
        frequencyView.setText("" + DataManager.getInstance().getReviewFrequency());

        speakView = getViewById(R.id.tv_speaker);
        speakView.setText(DataManager.getInstance().getTangoSpeaker());

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
                typeEdit.setText(DataManager.getInstance().getTangoType());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("学习的単語类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(typeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String type = typeEdit.getText().toString().trim();
                                DataManager.getInstance().setTangoType(type);
                                if (type.equals("")) {
                                    type = "全部";
                                }
                                typeView.setText(type);
                                showToast("更改成功");
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText partEdit = new EditText(getBaseContext());
                partEdit.setTextColor(Color.BLACK);
                partEdit.setTextSize(24);
                partEdit.setText(DataManager.getInstance().getPartOfSpeech());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("学习的単語词性")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(partEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String part = partEdit.getText().toString().trim();
                                DataManager.getInstance().setPartOfSpeech(part);
                                if (part.equals("")) {
                                    part = "全部";
                                }
                                partView.setText(part);
                                showToast("更改成功");
                                EventBus.getDefault().post(new LoadNewTangoEvent());
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
                goalEdit.setText("" + DataManager.getInstance().getTangoGoal());
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
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.getInstance().setTangoGoal(goal);
                                showToast("更改成功");
                                goalView.setText("" + goal);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_pronunciation_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText pronunciationTimeEdit = new EditText(getBaseContext());
                pronunciationTimeEdit.setTextColor(Color.BLACK);
                pronunciationTimeEdit.setTextSize(24);
                pronunciationTimeEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                pronunciationTimeEdit.setText("" + DataManager.getInstance().getPronunciationTime());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("发音延迟时间")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(pronunciationTimeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pronunciationTimeString = pronunciationTimeEdit.getText().toString();
                                float pronunciationTime = 0;
                                if (!pronunciationTimeString.equals("")) {
                                    pronunciationTime = Float.parseFloat(pronunciationTimeString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.getInstance().setPronunciationTime(pronunciationTime);
                                showToast("更改成功");
                                pronunciationTimeView.setText("" + pronunciationTime);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_writing_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText writingTimeEdit = new EditText(getBaseContext());
                writingTimeEdit.setTextColor(Color.BLACK);
                writingTimeEdit.setTextSize(24);
                writingTimeEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                writingTimeEdit.setText("" + DataManager.getInstance().getWritingTime());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("写法延迟时间")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(writingTimeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String writingTimeString = writingTimeEdit.getText().toString();
                                float writingTime = 0;
                                if (!writingTimeString.equals("")) {
                                    writingTime = Float.parseFloat(writingTimeString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.getInstance().setWritingTime(writingTime);
                                showToast("更改成功");
                                writingTimeView.setText("" + writingTime);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_meaning_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText meaningTimeEdit = new EditText(getBaseContext());
                meaningTimeEdit.setTextColor(Color.BLACK);
                meaningTimeEdit.setTextSize(24);
                meaningTimeEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                meaningTimeEdit.setText("" + DataManager.getInstance().getMeaningTime());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("解释延迟时间")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(meaningTimeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String meaningTimeString = meaningTimeEdit.getText().toString();
                                float meaningTime = 0;
                                if (!meaningTimeString.equals("")) {
                                    meaningTime = Float.parseFloat(meaningTimeString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.getInstance().setMeaningTime(meaningTime);
                                showToast("更改成功");
                                meaningTimeView.setText("" + meaningTime);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_frequency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText frequencyEdit = new EditText(getBaseContext());
                frequencyEdit.setTextColor(Color.BLACK);
                frequencyEdit.setTextSize(24);
                frequencyEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                frequencyEdit.setText("" + DataManager.getInstance().getReviewFrequency());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("复习系数")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(frequencyEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String frequencyString = frequencyEdit.getText().toString();
                                int frequency = 0;
                                if (!frequencyString.equals("")) {
                                    frequency = Integer.parseInt(frequencyString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.getInstance().setReviewFrequency(frequency);
                                showToast("更改成功");
                                frequencyView.setText("" + frequency);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
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
                                DataManager.getInstance().setTangoSpeaker(Constants.SPEAKERS[i]);
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
