package com.xmx.tango.core.activity;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.module.font.JapaneseFontDialog;
import com.xmx.tango.module.font.JapaneseFontChangeEvent;
import com.xmx.tango.module.operate.LoadNewTangoEvent;
import com.xmx.tango.module.speaker.SpeakTangoManager;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.tango.TangoConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    TextView missionView;
    TextView japaneseFontView;
    TextView speakView;
    EditText testView;
    TextView vibratorView;
    TextView serviceIntervalView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);

        typeView = getViewById(R.id.tv_type);
        String type = DataManager.INSTANCE.getTangoType();
        if (type.equals("")) {
            type = "全部";
        }
        typeView.setText(type);

        partView = getViewById(R.id.tv_part);
        String part = DataManager.INSTANCE.getPartOfSpeech();
        if (part.equals("")) {
            part = "全部";
        }
        partView.setText(part);

        goalView = getViewById(R.id.tv_goal);
        goalView.setText("" + DataManager.INSTANCE.getTangoGoal());

        pronunciationTimeView = getViewById(R.id.tv_pronunciation_time);
        pronunciationTimeView.setText("" + DataManager.INSTANCE.getPronunciationTime());

        writingTimeView = getViewById(R.id.tv_writing_time);
        writingTimeView.setText("" + DataManager.INSTANCE.getWritingTime());

        meaningTimeView = getViewById(R.id.tv_meaning_time);
        meaningTimeView.setText("" + DataManager.INSTANCE.getMeaningTime());

        frequencyView = getViewById(R.id.tv_frequency);
        frequencyView.setText("" + DataManager.INSTANCE.getReviewFrequency());

        missionView = getViewById(R.id.tv_mission_count);
        missionView.setText("" + DataManager.INSTANCE.getMissionCount());

        japaneseFontView = getViewById(R.id.tv_japanese_font);
        japaneseFontView.setText("あいうえお 日本語");
        AssetManager mgr = getAssets();
        String title = DataManager.INSTANCE.getJapaneseFontTitle();
        String font = null;
        if (title != null) {
            font = TangoConstants.INSTANCE.getJAPANESE_FONT_MAP().get(title);
        }
        Typeface tf = Typeface.DEFAULT;
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font);
        }
        japaneseFontView.setTypeface(tf);

        speakView = getViewById(R.id.tv_speaker);
        speakView.setText(DataManager.INSTANCE.getTangoSpeaker());

        testView = getViewById(R.id.edit_speak);

        vibratorView = getViewById(R.id.tv_vibrator);
        boolean vibratorFlag = DataManager.INSTANCE.getVibratorStatus();
        vibratorView.setText(vibratorFlag ? "开启" : "关闭");

        serviceIntervalView = getViewById(R.id.tv_service_interval);
        serviceIntervalView.setText("" + DataManager.INSTANCE.getServiceInterval());
    }

    @Override
    protected void setListener() {
        getViewById(R.id.layout_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText typeEdit = new EditText(getBaseContext());
                typeEdit.setTextColor(Color.BLACK);
                typeEdit.setTextSize(24);
                typeEdit.setText(DataManager.INSTANCE.getTangoType());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("学习的単語类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(typeEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String type = typeEdit.getText().toString().trim();
                                DataManager.INSTANCE.setTangoType(type);
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
                partEdit.setText(DataManager.INSTANCE.getPartOfSpeech());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("学习的単語词性")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(partEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String part = partEdit.getText().toString().trim();
                                DataManager.INSTANCE.setPartOfSpeech(part);
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
                goalEdit.setText("" + DataManager.INSTANCE.getTangoGoal());
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
                                DataManager.INSTANCE.setTangoGoal(goal);
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
                pronunciationTimeEdit.setText("" + DataManager.INSTANCE.getPronunciationTime());
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
                                DataManager.INSTANCE.setPronunciationTime(pronunciationTime);
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
                writingTimeEdit.setText("" + DataManager.INSTANCE.getWritingTime());
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
                                DataManager.INSTANCE.setWritingTime(writingTime);
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
                meaningTimeEdit.setText("" + DataManager.INSTANCE.getMeaningTime());
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
                                DataManager.INSTANCE.setMeaningTime(meaningTime);
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
                frequencyEdit.setText("" + DataManager.INSTANCE.getReviewFrequency());
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
                                DataManager.INSTANCE.setReviewFrequency(frequency);
                                showToast("更改成功");
                                frequencyView.setText("" + frequency);
                                EventBus.getDefault().post(new LoadNewTangoEvent());
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_mission_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText missionEdit = new EditText(getBaseContext());
                missionEdit.setTextColor(Color.BLACK);
                missionEdit.setTextSize(24);
                missionEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                missionEdit.setText("" + DataManager.INSTANCE.getMissionCount());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("任务模式単語数")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(missionEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String missionString = missionEdit.getText().toString();
                                int mission = 0;
                                if (!missionString.equals("")) {
                                    mission = Integer.parseInt(missionString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.INSTANCE.setMissionCount(mission);
                                showToast("更改成功");
                                missionView.setText("" + mission);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.layout_japanese_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JapaneseFontDialog dialog = new JapaneseFontDialog();
                dialog.initDialog(SettingActivity.this);
                dialog.show(getFragmentManager(), "JAPANESE_FONT");
            }
        });

        getViewById(R.id.layout_speaker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("朗读音色")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(TangoConstants.INSTANCE.getSPEAKERS(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataManager.INSTANCE.setTangoSpeaker(TangoConstants.INSTANCE.getSPEAKERS()[i]);
                                speakView.setText(TangoConstants.INSTANCE.getSPEAKERS()[i]);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        getViewById(R.id.btn_speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = testView.getText().toString();
                SpeakTangoManager.INSTANCE.speak(SettingActivity.this, text);
            }
        });

        getViewById(R.id.layout_vibrator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean vibratorFlag = DataManager.INSTANCE.getVibratorStatus();
                vibratorFlag = !vibratorFlag;
                DataManager.INSTANCE.setVibratorStatus(vibratorFlag);
                vibratorView.setText(vibratorFlag ? "开启" : "关闭");
            }
        });

        getViewById(R.id.layout_service_interval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText serviceIntervalEdit = new EditText(getBaseContext());
                serviceIntervalEdit.setTextColor(Color.BLACK);
                serviceIntervalEdit.setTextSize(24);
                serviceIntervalEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                serviceIntervalEdit.setText("" + DataManager.INSTANCE.getServiceInterval());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("服务换词间隔(ms)")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(serviceIntervalEdit)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String serviceIntervalString = serviceIntervalEdit.getText().toString();
                                int serviceInterval = 0;
                                if (!serviceIntervalString.equals("")) {
                                    serviceInterval = Integer.parseInt(serviceIntervalString);
                                } else {
                                    showToast("更改失败");
                                    return;
                                }
                                DataManager.INSTANCE.setServiceInterval(serviceInterval);
                                showToast("更改成功");
                                serviceIntervalView.setText("" + serviceInterval);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(JapaneseFontChangeEvent event) {
        AssetManager mgr = getAssets();
        String title = DataManager.INSTANCE.getJapaneseFontTitle();
        String font = TangoConstants.INSTANCE.getJAPANESE_FONT_MAP().get(title);
        Typeface tf = Typeface.DEFAULT;
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font);
        }
        japaneseFontView.setTypeface(tf);
    }
}
