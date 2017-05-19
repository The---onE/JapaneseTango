package com.xmx.tango.module.mission;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.module.tango.JapaneseFontChangeEvent;
import com.xmx.tango.module.tango.LoadNewTangoEvent;
import com.xmx.tango.module.tango.SpeakTangoManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.tango.TangoOperator;
import com.xmx.tango.module.tango.VerbDialog;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.utils.Timer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_mission)
public class MissionActivity extends BaseTempActivity {
    Tango tango;
    Tango prevTango;
    int prevOperate;

    Timer pronunciationTimer = new Timer() {
        @Override
        public void timer() {
            showPronunciation();
            checkAnswer();
        }
    };
    boolean pronunciationFlag = false;
    Timer writingTimer = new Timer() {
        @Override
        public void timer() {
            showWriting();
            checkAnswer();
        }
    };
    boolean writingFlag = false;
    Timer meaningTimer = new Timer() {
        @Override
        public void timer() {
            showMeaning();
            checkAnswer();
        }
    };
    boolean meaningFlag = false;

    @ViewInject(R.id.tv_tango_pronunciation)
    private TextView pronunciationView;
    @ViewInject(R.id.tv_tango_tone)
    private TextView toneView;
    @ViewInject(R.id.tv_tango_writing)
    private TextView writingView;
    @ViewInject(R.id.tv_tango_meaning)
    private TextView meaningView;
    @ViewInject(R.id.tv_tango_part)
    private TextView partView;
    @ViewInject(R.id.tv_tango_count)
    private TextView countView;
    @ViewInject(R.id.tv_tango_prev)
    private TextView prevView;

    @ViewInject(R.id.btn_remember)
    private Button rememberButton;
    @ViewInject(R.id.btn_forget)
    private Button forgetButton;
    @ViewInject(R.id.btn_answer)
    private Button answerButton;
    @ViewInject(R.id.line_answer)
    private View answerLine;

    @Event(value = R.id.btn_remember)
    private void onRememberClick(View view) {
        operateTango(REMEMBER);
    }

    @Event(value = R.id.btn_forget)
    private void onForgetClick(View view) {
        operateTango(FORGET);
    }

    @Event(value = R.id.btn_answer)
    private void onAnswerClick(View view) {
        showAnswer();
    }

    @Event(value = R.id.tv_tango_writing)
    private void onWritingClick(View view) {
        String writing = writingView.getText().toString();
        if (!writing.equals("")) {
            SpeakTangoManager.getInstance().speak(writing);
        }
    }

    @Event(value = R.id.tv_tango_pronunciation)
    private void onPronunciationClick(View view) {
        String pronunciation = pronunciationView.getText().toString();
        if (!pronunciation.equals("")) {
            SpeakTangoManager.getInstance().speak(pronunciation);
        }
    }

    @Event(value = R.id.tv_tango_part)
    private void onPartClick(View view) {
        String part = partView.getText().toString();
        if (!part.equals("")) {
            if (part.contains(TangoConstants.VERB_FLAG)) {
                String verb = tango.writing;

                int type = 0;
                switch (tango.partOfSpeech) {
                    case TangoConstants.VERB1_FLAG:
                        type = 1;
                        break;
                    case TangoConstants.VERB2_FLAG:
                        type = 2;
                        break;
                    case TangoConstants.VERB3_FLAG:
                        type = 3;
                        break;
                }

                VerbDialog dialog = new VerbDialog();
                dialog.initDialog(this, verb, type);
                dialog.show(getFragmentManager(), "VERB");
            }
        }
    }

    @Event(value = R.id.tv_tango_prev)
    private void onPrevClick(View view) {
        TangoOperator.getInstance().cancelOperate();
        tango = null;
        loadNewTango(prevTango);
        TangoManager.getInstance().addToWaitingList(tango);
        int count = TangoManager.getInstance().getWaitingList().size();
        countView.setText("任务剩余：" + count +
                "\n任务已记：" + (DataManager.getInstance().getMissionCount() - count));
    }

    Random random = new Random();

    private boolean operateFlag = true;
    private static final int REMEMBER = 1;
    private static final int FORGET = 2;
    private static final int REMEMBER_FOREVER = 3;

    private boolean operateTango(final int operation) {
        if (operateFlag) {
            operateFlag = false;
            rememberButton.setBackgroundColor(Color.LTGRAY);
            forgetButton.setBackgroundColor(Color.LTGRAY);
            prevOperate = operation;
            if (tango != null && tango.id > 0) {
                switch (operation) {
                    case REMEMBER:
                        TangoOperator.getInstance().remember(tango);
                        TangoManager.getInstance().removeFromWaitingList(tango);
                        break;
                    case FORGET:
                        TangoOperator.getInstance().forget(tango);
                        break;
                    case REMEMBER_FOREVER:
                        TangoOperator.getInstance().rememberForever(tango);
                        TangoManager.getInstance().removeFromWaitingList(tango);
                        break;
                }
                int count = TangoManager.getInstance().getWaitingList().size();
                if (count <= 0) {
                    showToast("任务完成！");
                    DataManager.getInstance()
                            .setTodayMission(DataManager.getInstance().getTodayMission() + 1);
                    finish();
                }
                countView.setText("任务剩余：" + count +
                        "\n任务已记：" + (DataManager.getInstance().getMissionCount() - count));

                loadNew();
                return true;
            } else {
                loadNew();
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setJapaneseFont();

        rememberButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        WindowManager wm = (WindowManager) getApplicationContext()
                                .getSystemService(Context.WINDOW_SERVICE);
                        int h = wm.getDefaultDisplay().getHeight();
                        float y = h - motionEvent.getRawY();
                        if (y > h / 3) {
                            operateTango(REMEMBER_FOREVER);
                        }
                        break;
                }
                return false;
            }
        });

        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.getInstance().study >= goal;
        TangoManager.getInstance().updateWaitingList(reviewFlag,
                DataManager.getInstance().getReviewFrequency(),
                DataManager.getInstance().getMissionCount());

        int count = TangoManager.getInstance().getWaitingList().size();
        countView.setText("任务剩余：" + count +
                "\n任务已记：" + (DataManager.getInstance().getMissionCount() - count));

        loadNewTango();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("任务还未完成，确认要退出吗？")
                .setTitle("提示")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new LoadNewTangoEvent());
    }

    private void checkAnswer() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            //answerButton.setVisibility(View.GONE);
            answerLine.setVisibility(View.GONE);
            answerButton.setEnabled(false);
            ObjectAnimator animator = ObjectAnimator.ofFloat(answerButton, "alpha", 1f, 0f); // 渐出效果
            animator.setDuration(300);
            animator.start();
        }
    }

    private void showTextView(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f); // 渐入效果
        animator.setDuration(300);
        animator.start();
    }

    private void showPronunciation() {
        //pronunciationView.setVisibility(View.VISIBLE);
        showTextView(pronunciationView);
        //toneView.setVisibility(View.VISIBLE);
        showTextView(toneView);
        pronunciationFlag = true;
    }

    private void delayPronunciation() {
        if (pronunciationTimer != null) {
            pronunciationTimer.stop();
            pronunciationTimer.start((long) (DataManager.getInstance().getPronunciationTime() * 1000), true);
        }
    }

    private void showWriting() {
        //writingView.setVisibility(View.VISIBLE);
        showTextView(writingView);
        writingFlag = true;
    }

    private void delayWriting() {
        if (writingTimer != null) {
            writingTimer.stop();
            writingTimer.start((long) (DataManager.getInstance().getWritingTime() * 1000), true);
        }
    }

    private void showMeaning() {
        //partView.setVisibility(View.VISIBLE);
        showTextView(partView);
        //meaningView.setVisibility(View.VISIBLE);
        showTextView(meaningView);
        meaningFlag = true;
    }

    private void delayMeaning() {
        if (meaningTimer != null) {
            meaningTimer.stop();
            meaningTimer.start((long) (DataManager.getInstance().getMeaningTime() * 1000), true);
        }
    }

    private void loadNew() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            loadNewTango();
        } else {
            showAnswer();
            new Timer() {
                @Override
                public void timer() {
                    loadNewTango();
                }
            }.start(TangoConstants.NEW_TANGO_DELAY, true);
        }
    }

    private float measureWidth(TextView textView) {
        return textView.getPaint().measureText(textView.getText().toString());
    }

    private void loadNewTango() {
        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.getInstance().study >= goal;
        Tango temp = TangoManager.getInstance().randomTango(reviewFlag,
                DataManager.getInstance().getReviewFrequency(), tango, true);
        loadNewTango(temp);
    }

    private void loadNewTango(Tango newTango) {
        pronunciationFlag = false;
        writingFlag = false;
        meaningFlag = false;
        //answerButton.setVisibility(View.VISIBLE);
        answerLine.setVisibility(View.VISIBLE);
        answerButton.setEnabled(true);
        ObjectAnimator animator = ObjectAnimator.ofFloat(answerButton, "alpha", 0f, 1f); // 渐入效果
        animator.setDuration(300);
        animator.start();
        new Timer() {
            @Override
            public void timer() {
                operateFlag = true;
                rememberButton.setBackgroundColor(Color.TRANSPARENT);
                forgetButton.setBackgroundColor(Color.TRANSPARENT);
            }
        }.start(TangoConstants.INTERVAL_TIME_MIN, true);

        if (tango != null) {
            prevTango = tango;
            showPrevTango();
        } else {
            prevView.setText("");
        }

        tango = newTango;

        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int textSize = 0;

        textSize = TangoConstants.DEFAULT_PRONUNCIATION_TEXT_SIZE;
        pronunciationView.setTextSize(textSize);
        pronunciationView.setText(tango.pronunciation);
        pronunciationView.setVisibility(View.INVISIBLE);
        toneView.setTextSize(textSize);
        if (tango.tone >= 0 && tango.tone < TangoConstants.TONES.length) {
            toneView.setText(TangoConstants.TONES[tango.tone]);
        } else {
            toneView.setText("");
        }
        toneView.setVisibility(View.INVISIBLE);
        float pronunciationLength = measureWidth(pronunciationView) + measureWidth(toneView);
        while (pronunciationLength > width) {
            textSize -= 1;
            pronunciationView.setTextSize(textSize);
            toneView.setTextSize(textSize);
            pronunciationLength = measureWidth(pronunciationView) + measureWidth(toneView);
        }

        textSize = TangoConstants.DEFAULT_WRITING_TEXT_SIZE;
        writingView.setTextSize(textSize);
        writingView.setText(tango.writing);
        writingView.setVisibility(View.INVISIBLE);
        float writingLength = measureWidth(writingView);
        while (writingLength > width) {
            textSize -= 1;
            writingView.setTextSize(textSize);
            writingLength = measureWidth(writingView);
        }

        textSize = TangoConstants.DEFAULT_MEANING_TEXT_SIZE;
        partView.setTextSize(textSize);
        if (!tango.partOfSpeech.equals("")) {
            partView.setText("[" + tango.partOfSpeech + "]");
        } else {
            partView.setText("");
        }
        partView.setVisibility(View.INVISIBLE);
        meaningView.setTextSize(TangoConstants.DEFAULT_MEANING_TEXT_SIZE);
        meaningView.setText(tango.meaning);
        meaningView.setVisibility(View.INVISIBLE);
        float meaningLength = measureWidth(meaningView) + measureWidth(partView);
        while (meaningLength > width) {
            textSize -= 1;
            partView.setTextSize(textSize);
            meaningView.setTextSize(textSize);
            meaningLength = measureWidth(meaningView) + measureWidth(partView);
        }

        int i = random.nextInt(3);
        boolean r = random.nextBoolean();
        if (tango.pronunciation.equals("")) {
            showPronunciation();
            if (r) {
                showWriting();
                delayMeaning();
            } else {
                delayWriting();
                showMeaning();
            }
        } else if (tango.writing.equals("")) {
            showWriting();
            if (r) {
                showPronunciation();
                delayMeaning();
            } else {
                delayPronunciation();
                showMeaning();
            }
        } else if (tango.meaning.equals("")) {
            showMeaning();
            if (r) {
                showPronunciation();
                delayWriting();
            } else {
                delayPronunciation();
                showWriting();
            }
        } else if (tango.pronunciation.equals(tango.writing)) {
            if (r) {
                showPronunciation();
                showWriting();
                delayMeaning();
            } else {
                int pTime = (int) DataManager.getInstance().getPronunciationTime() * 1000;
                int wTime = (int) DataManager.getInstance().getWritingTime() * 1000;
                int time = Math.min(pTime, wTime);
                //delayPronunciation();
                if (pronunciationTimer != null) {
                    pronunciationTimer.stop();
                    pronunciationTimer.start(time, true);
                }
                //delayWriting();
                if (writingTimer != null) {
                    writingTimer.stop();
                    writingTimer.start(time, true);
                }
                showMeaning();
            }
        } else {
            switch (i) {
                case 0:
                    showPronunciation();
                    delayWriting();
                    delayMeaning();
                    break;

                case 1:
                    delayPronunciation();
                    showWriting();
                    delayMeaning();
                    break;

                case 2:
                    delayPronunciation();
                    delayWriting();
                    showMeaning();
                    break;

                default:
                    showPronunciation();
                    showWriting();
                    showMeaning();
            }
        }
    }

    private void showPrevTango() {
        String text = "";
        switch (prevOperate) {
            case REMEMBER:
                text = "√ ";
                break;
            case FORGET:
                text = "× ";
                break;
            case REMEMBER_FOREVER:
                text = "√ ";
                break;
        }
        text += prevTango.pronunciation + "\n";
        if (!prevTango.writing.equals(prevTango.pronunciation)) {
            text += prevTango.writing + "\n";
        }
        if (!prevTango.partOfSpeech.equals("")) {
            text += "[" + prevTango.partOfSpeech + "]";
        }
        text += prevTango.meaning;
        prevView.setText(text);
    }

    private void showAnswer() {
        if (!pronunciationFlag && pronunciationTimer != null) {
            pronunciationTimer.execute();
            pronunciationTimer.stop();
        }

        if (!writingFlag && writingTimer != null) {
            writingTimer.execute();
            writingTimer.stop();
        }

        if (!meaningFlag && meaningTimer != null) {
            meaningTimer.execute();
            meaningTimer.stop();
        }
    }

    private void setJapaneseFont() {
        AssetManager mgr = getAssets();
        String title = DataManager.getInstance().getJapaneseFontTitle();
        String font = null;
        if (title != null) {
            font = TangoConstants.JAPANESE_FONT_MAP.get(title);
        }
        Typeface tf = Typeface.DEFAULT;
        if (font != null) {
            tf = Typeface.createFromAsset(mgr, font);
        }
        pronunciationView.setTypeface(tf);
        writingView.setTypeface(tf);
    }

    @Subscribe
    public void onEvent(LoadNewTangoEvent event) {
        loadNewTango();
    }

    @Subscribe
    public void onEvent(JapaneseFontChangeEvent event) {
        setJapaneseFont();
    }
}
