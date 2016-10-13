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

    Timer answerTimer;
    boolean answerFlag = false;
    Timer meaningTimer;
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

    private void loadNew() {
        if (answerFlag && meaningFlag) {
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
        answerFlag = false;
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

        boolean r = random.nextBoolean();
        if (r) {
            pronunciationView.setVisibility(View.VISIBLE);
            toneView.setVisibility(View.VISIBLE);
            if (answerTimer != null) {
                answerTimer.stop();
            }
            answerTimer = new Timer() {
                @Override
                public void timer() {
                    writingView.setVisibility(View.VISIBLE);
                    answerFlag = true;
                    if (meaningFlag) {
                        answerButton.setVisibility(View.GONE);
                    }
                }
            };
            answerTimer.start((int) DataManager.getInstance().getAnswerTime() * 1000, true);
        } else {
            writingView.setVisibility(View.VISIBLE);
            if (answerTimer != null) {
                answerTimer.stop();
            }
            answerTimer = new Timer() {
                @Override
                public void timer() {
                    pronunciationView.setVisibility(View.VISIBLE);
                    toneView.setVisibility(View.VISIBLE);

                    answerFlag = true;
                    if (meaningFlag) {
                        answerButton.setVisibility(View.GONE);
                    }
                }
            };
            answerTimer.start((int) DataManager.getInstance().getAnswerTime() * 1000, true);
        }

        if (meaningTimer != null) {
            meaningTimer.stop();
        }
        meaningTimer = new Timer() {
            @Override
            public void timer() {
                partView.setVisibility(View.VISIBLE);
                meaningView.setVisibility(View.VISIBLE);

                meaningFlag = true;
                if (answerFlag) {
                    answerButton.setVisibility(View.GONE);
                }
            }
        };
        meaningTimer.start((int) DataManager.getInstance().getMeaningTime() * 1000, true);
    }

    private void showAnswer() {
        if (!answerFlag && answerTimer != null) {
            answerTimer.execute();
            answerTimer.stop();
        }

        if (!meaningFlag && meaningTimer != null) {
            meaningTimer.execute();
            meaningTimer.stop();
        }
    }
}
