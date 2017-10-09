package com.xmx.tango.core.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.module.crud.ChooseTangoEvent;
import com.xmx.tango.module.font.JapaneseFontChangeEvent;
import com.xmx.tango.module.operate.LoadNewTangoEvent;
import com.xmx.tango.module.speaker.SpeakTangoManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.operate.TangoOperator;
import com.xmx.tango.module.verb.VerbDialog;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.base.fragment.xUtilsFragment;
import com.xmx.tango.utils.Timer;
import com.xmx.tango.utils.VibratorUtil;

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
            SpeakTangoManager.INSTANCE.speak(getContext(), writing);
        }
    }

    @Event(value = R.id.tv_tango_pronunciation)
    private void onPronunciationClick(View view) {
        String pronunciation = pronunciationView.getText().toString();
        if (!pronunciation.equals("")) {
            SpeakTangoManager.INSTANCE.speak(getContext(), pronunciation);
        }
    }

    @Event(value = R.id.tv_tango_part)
    private void onPartClick(View view) {
        String part = partView.getText().toString();
        if (!part.equals("")) {
            if (part.contains(TangoConstants.INSTANCE.getVERB_FLAG())) {
                String verb = tango.getWriting();
                int type = 0;
                String p = tango.getPartOfSpeech();
                if (TangoConstants.INSTANCE.getVERB1_FLAG().equals(p)) {
                    type = 1;
                } else if (TangoConstants.INSTANCE.getVERB2_FLAG().equals(p)) {
                    type = 2;
                } else if (TangoConstants.INSTANCE.getVERB3_FLAG().equals(p)) {
                    type = 3;
                }

                VerbDialog dialog = new VerbDialog();
                dialog.initDialog(getContext(), verb, type);
                dialog.show(getActivity().getFragmentManager(), "VERB");
            }
        }
    }

    @Event(value = R.id.tv_tango_prev)
    private void onPrevClick(View view) {
        TangoOperator.INSTANCE.cancelOperate();
        tango = null;
        loadNewTango(prevTango);
        countView.setText("今日复习：" + TangoOperator.INSTANCE.getReview() +
                "\n今日已记：" + TangoOperator.INSTANCE.getStudy() +
                "\n今日完成任务：" + DataManager.getInstance().getTodayMission());
    }

    @Event(value = R.id.layout_tango, type = View.OnLongClickListener.class)
    private boolean onTangoLongClick(View view) {
        EventBus.getDefault().post(new ChooseTangoEvent(tango));
        return true;
    }

    @Event(value = R.id.tv_tango_writing, type = View.OnLongClickListener.class)
    private boolean onTangoWritingLongClick(View view) {
        EventBus.getDefault().post(new ChooseTangoEvent(tango));
        return true;
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
            if (tango != null && tango.getId() > 0) {
                switch (operation) {
                    case REMEMBER:
                        TangoOperator.INSTANCE.remember(tango);
                        if (DataManager.getInstance().getVibratorStatus()) {
                            VibratorUtil.INSTANCE.vibrate(getContext(),
                                    TangoConstants.INSTANCE.getREMEMBER_VIBRATE_TIME());
                        }
                        break;
                    case FORGET:
                        TangoOperator.INSTANCE.forget(tango);
                        if (DataManager.getInstance().getVibratorStatus()) {
                            VibratorUtil.INSTANCE.vibrate(getContext(),
                                    TangoConstants.INSTANCE.getFORGET_VIBRATE_TIME());
                        }
                        break;
                    case REMEMBER_FOREVER:
                        TangoOperator.INSTANCE.rememberForever(tango);
                        if (DataManager.getInstance().getVibratorStatus()) {
                            VibratorUtil.INSTANCE.vibrate(getContext(),
                                    TangoConstants.INSTANCE.getREMEMBER_FOREVER_VIBRATE_TIME());
                        }
                        break;
                }
                countView.setText("今日复习：" + TangoOperator.INSTANCE.getReview() +
                        "\n今日已记：" + TangoOperator.INSTANCE.getStudy() +
                        "\n今日完成任务：" + DataManager.getInstance().getTodayMission());

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
        setJapaneseFont();

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

        countView.setText("今日复习：" + TangoOperator.INSTANCE.getReview() +
                "\n今日已记：" + TangoOperator.INSTANCE.getStudy() +
                "\n今日完成任务：" + DataManager.getInstance().getTodayMission());

        loadNewTango();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
            }.start(TangoConstants.INSTANCE.getNEW_TANGO_DELAY(), true);
        }
    }

    private float measureWidth(TextView textView) {
        return textView.getPaint().measureText(textView.getText().toString());
    }

    private void loadNewTango() {
        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.INSTANCE.getStudy() >= goal;
        Tango temp = TangoManager.INSTANCE.randomTango(reviewFlag,
                DataManager.getInstance().getReviewFrequency(), tango, false);
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
        }.start(TangoConstants.INSTANCE.getINTERVAL_TIME_MIN(), true);

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

        textSize = TangoConstants.INSTANCE.getDEFAULT_PRONUNCIATION_TEXT_SIZE();
        pronunciationView.setTextSize(textSize);
        pronunciationView.setText(tango.getPronunciation());
        pronunciationView.setVisibility(View.INVISIBLE);
        toneView.setTextSize(textSize);
        if (tango.getTone() >= 0 && tango.getTone() < TangoConstants.INSTANCE.getTONES().length) {
            toneView.setText(TangoConstants.INSTANCE.getTONES()[tango.getTone()]);
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

        textSize = TangoConstants.INSTANCE.getDEFAULT_WRITING_TEXT_SIZE();
        writingView.setTextSize(textSize);
        writingView.setText(tango.getWriting());
        writingView.setVisibility(View.INVISIBLE);
        float writingLength = measureWidth(writingView);
        while (writingLength > width) {
            textSize -= 1;
            writingView.setTextSize(textSize);
            writingLength = measureWidth(writingView);
        }

        textSize = TangoConstants.INSTANCE.getDEFAULT_MEANING_TEXT_SIZE();
        partView.setTextSize(textSize);
        if (!tango.getPartOfSpeech().equals("")) {
            partView.setText("[" + tango.getPartOfSpeech() + "]");
        } else {
            partView.setText("");
        }
        partView.setVisibility(View.INVISIBLE);
        meaningView.setTextSize(TangoConstants.INSTANCE.getDEFAULT_MEANING_TEXT_SIZE());
        meaningView.setText(tango.getMeaning());
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
        if (tango.getPronunciation().equals("")) {
            showPronunciation();
            if (r) {
                showWriting();
                delayMeaning();
            } else {
                delayWriting();
                showMeaning();
            }
        } else if (tango.getWriting().equals("")) {
            showWriting();
            if (r) {
                showPronunciation();
                delayMeaning();
            } else {
                delayPronunciation();
                showMeaning();
            }
        } else if (tango.getMeaning().equals("")) {
            showMeaning();
            if (r) {
                showPronunciation();
                delayWriting();
            } else {
                delayPronunciation();
                showWriting();
            }
        } else if (tango.getPronunciation().equals(tango.getWriting())) {
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
        text += prevTango.getPronunciation() + "\n";
        if (!prevTango.getWriting().equals(prevTango.getPronunciation())) {
            text += prevTango.getWriting() + "\n";
        }
        if (!prevTango.getPartOfSpeech().equals("")) {
            text += "[" + prevTango.getPartOfSpeech() + "]";
        }
        text += prevTango.getMeaning();
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

    @Override
    public void onResume() {
        super.onResume();
        countView.setText("今日复习：" + TangoOperator.INSTANCE.getReview() +
                "\n今日已记：" + TangoOperator.INSTANCE.getStudy() +
                "\n今日完成任务：" + DataManager.getInstance().getTodayMission());
    }

    private void setJapaneseFont() {
        AssetManager mgr = getContext().getAssets();
        String title = DataManager.getInstance().getJapaneseFontTitle();
        String font = null;
        if (title != null) {
            font = TangoConstants.INSTANCE.getJAPANESE_FONT_MAP().get(title);
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
