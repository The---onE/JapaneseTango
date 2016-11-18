package com.xmx.tango.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.tango.Constants;
import com.xmx.tango.R;
import com.xmx.tango.Tango.LoadNewTangoEvent;
import com.xmx.tango.Tango.SpeakTangoManager;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tango.TangoOperator;
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;
import com.xmx.tango.Tools.Timer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends xUtilsFragment {

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

    @Event(value = R.id.tv_tango_prev)
    private void onPrevClick(View view) {
        TangoOperator.getInstance().cancelOperate();
        tango = null;
        loadNewTango(prevTango);
        countView.setText("今日复习：" + TangoOperator.getInstance().review +
                "\n今日已记：" + TangoOperator.getInstance().study);
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
                        break;
                    case FORGET:
                        TangoOperator.getInstance().forget(tango);
                        break;
                    case REMEMBER_FOREVER:
                        TangoOperator.getInstance().rememberForever(tango);
                        break;
                }
                countView.setText("今日复习：" + TangoOperator.getInstance().review +
                        "\n今日已记：" + TangoOperator.getInstance().study);

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
    protected void processLogic(Bundle savedInstanceState) {
        rememberButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        WindowManager wm = (WindowManager) getContext().getApplicationContext()
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

        countView.setText("今日复习：" + TangoOperator.getInstance().review +
                "\n今日已记：" + TangoOperator.getInstance().study);

        loadNewTango();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void checkAnswer() {
        if (pronunciationFlag && writingFlag && meaningFlag) {
            answerButton.setVisibility(View.GONE);
        }
    }

    private void showPronunciation() {
        pronunciationView.setVisibility(View.VISIBLE);
        toneView.setVisibility(View.VISIBLE);
        pronunciationFlag = true;
    }

    private void delayPronunciation() {
        if (pronunciationTimer != null) {
            pronunciationTimer.stop();
            pronunciationTimer.start((long) (DataManager.getInstance().getPronunciationTime() * 1000), true);
        }
    }

    private void showWriting() {
        writingView.setVisibility(View.VISIBLE);
        writingFlag = true;
    }

    private void delayWriting() {
        if (writingTimer != null) {
            writingTimer.stop();
            writingTimer.start((long) (DataManager.getInstance().getWritingTime() * 1000), true);
        }
    }

    private void showMeaning() {
        partView.setVisibility(View.VISIBLE);
        meaningView.setVisibility(View.VISIBLE);
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
            }.start(Constants.NEW_TANGO_DELAY, true);
        }
    }

    private float measureWidth(TextView textView) {
        return textView.getPaint().measureText(textView.getText().toString());
    }

    private void loadNewTango() {
        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.getInstance().study >= goal;
        Tango temp = TangoManager.getInstance().randomTango(reviewFlag,
                DataManager.getInstance().getReviewFrequency(), tango);
        loadNewTango(temp);
    }

    private void loadNewTango(Tango newTango) {
        pronunciationFlag = false;
        writingFlag = false;
        meaningFlag = false;
        answerButton.setVisibility(View.VISIBLE);
        new Timer() {
            @Override
            public void timer() {
                operateFlag = true;
                rememberButton.setBackgroundColor(Color.TRANSPARENT);
                forgetButton.setBackgroundColor(Color.TRANSPARENT);
            }
        }.start(Constants.INTERVAL_TIME_MIN, true);

        if (tango != null) {
            prevTango = tango;
            showPrevTango();
        } else {
            prevView.setText("");
        }

        tango = newTango;

        WindowManager wm = (WindowManager) getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int textSize = 0;

        textSize = Constants.DEFAULT_PRONUNCIATION_TEXT_SIZE;
        pronunciationView.setTextSize(textSize);
        pronunciationView.setText(tango.pronunciation);
        pronunciationView.setVisibility(View.INVISIBLE);
        toneView.setTextSize(textSize);
        if (tango.tone >= 0 && tango.tone < Constants.TONES.length) {
            toneView.setText(Constants.TONES[tango.tone]);
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

        textSize = Constants.DEFAULT_WRITING_TEXT_SIZE;
        writingView.setTextSize(textSize);
        writingView.setText(tango.writing);
        writingView.setVisibility(View.INVISIBLE);
        float writingLength = measureWidth(writingView);
        while (writingLength > width) {
            textSize -= 1;
            writingView.setTextSize(textSize);
            writingLength = measureWidth(writingView);
        }

        textSize = Constants.DEFAULT_MEANING_TEXT_SIZE;
        partView.setTextSize(textSize);
        if (!tango.partOfSpeech.equals("")) {
            partView.setText("[" + tango.partOfSpeech + "]");
        } else {
            partView.setText("");
        }
        partView.setVisibility(View.INVISIBLE);
        meaningView.setTextSize(Constants.DEFAULT_MEANING_TEXT_SIZE);
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

    @Subscribe
    public void onEvent(LoadNewTangoEvent event) {
        loadNewTango();
    }
}
