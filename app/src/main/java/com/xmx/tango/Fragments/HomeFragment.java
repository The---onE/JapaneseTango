package com.xmx.tango.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xmx.tango.Constants;
import com.xmx.tango.R;
import com.xmx.tango.Tango.SpeakTangoManager;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tango.TangoOperator;
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.Tools.FragmentBase.BaseFragment;
import com.xmx.tango.Tools.Timer;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    Tango tango;

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

    TextView pronunciationView;
    TextView toneView;
    TextView writingView;
    TextView meaningView;
    TextView partView;
    TextView countView;

    Button rememberButton;
    Button forgetButton;
    Button answerButton;

    Random random = new Random();

    private boolean operateFlag = true;
    private static final int REMEMBER = 1;
    private static final int FORGET = 2;
    private static final int REMEMBER_FOREVER = 3;

    private boolean operateTango(final int operation) {
        if (tango != null && tango.id > 0 && operateFlag) {
            operateFlag = false;
            rememberButton.setBackgroundColor(Color.LTGRAY);
            forgetButton.setBackgroundColor(Color.LTGRAY);

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
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initView(View view) {
        pronunciationView = (TextView) view.findViewById(R.id.tv_tango_pronunciation);
        toneView = (TextView) view.findViewById(R.id.tv_tango_tone);
        writingView = (TextView) view.findViewById(R.id.tv_tango_writing);
        meaningView = (TextView) view.findViewById(R.id.tv_tango_meaning);
        partView = (TextView) view.findViewById(R.id.tv_tango_part);
        countView = (TextView) view.findViewById(R.id.tv_tango_count);

        rememberButton = (Button) view.findViewById(R.id.btn_remember);
        forgetButton = (Button) view.findViewById(R.id.btn_forget);
        answerButton = (Button) view.findViewById(R.id.btn_answer);

        countView.setText("今日复习：" + TangoOperator.getInstance().review +
                "\n今日已记：" + TangoOperator.getInstance().study);
    }

    @Override
    protected void setListener(View view) {
        rememberButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        WindowManager wm = (WindowManager
                                ) getContext().getApplicationContext()
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

        rememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operateTango(REMEMBER);
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operateTango(FORGET);
            }
        });

        writingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                if (!writing.equals("")) {
                    SpeakTangoManager.getInstance().speak(writing);
                }
            }
        });

        pronunciationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pronunciation = pronunciationView.getText().toString();
                if (!pronunciation.equals("")) {
                    SpeakTangoManager.getInstance().speak(pronunciation);
                }
            }
        });

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnswer();
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {
        loadNewTango();
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
            pronunciationTimer.start((int) DataManager.getInstance().getPronunciationTime() * 1000, true);
        }
    }

    private void showWriting() {
        writingView.setVisibility(View.VISIBLE);
        writingFlag = true;
    }

    private void delayWriting() {
        if (writingTimer != null) {
            writingTimer.stop();
            writingTimer.start((int) DataManager.getInstance().getWritingTime() * 1000, true);
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
            meaningTimer.start((int) DataManager.getInstance().getMeaningTime() * 1000, true);
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

    private void loadNewTango() {
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

        int goal = DataManager.getInstance().getTangoGoal();
        boolean reviewFlag = TangoOperator.getInstance().study >= goal;
        Tango temp = TangoManager.getInstance().randomTango(reviewFlag, DataManager.getInstance().getReviewFrequency());
        if (tango != null && temp.id == tango.id) {
            tango = TangoManager.getInstance().nextTango(reviewFlag, DataManager.getInstance().getReviewFrequency());
        } else {
            tango = temp;
        }

        pronunciationView.setText(tango.pronunciation);
        pronunciationView.setVisibility(View.INVISIBLE);
        if (tango.tone >= 0 && tango.tone < Constants.TONES.length) {
            toneView.setText(Constants.TONES[tango.tone]);
        } else {
            toneView.setText("");
        }
        toneView.setVisibility(View.INVISIBLE);
        writingView.setText(tango.writing);
        writingView.setVisibility(View.INVISIBLE);
        if (!tango.partOfSpeech.equals("")) {
            partView.setText("[" + tango.partOfSpeech + "]");
        } else {
            partView.setText("");
        }
        partView.setVisibility(View.INVISIBLE);
        meaningView.setText(tango.meaning);
        meaningView.setVisibility(View.INVISIBLE);

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
}
