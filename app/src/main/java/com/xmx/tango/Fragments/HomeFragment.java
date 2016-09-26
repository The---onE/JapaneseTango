package com.xmx.tango.Fragments;

import android.content.Context;
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
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.Tools.FragmentBase.BaseFragment;
import com.xmx.tango.Tools.Timer;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    Tango tango;
    int count;
    int review;
    int todayConsecutive = 0;

    Timer answerTimer;
    Timer meaningTimer;

    TextView pronunciationView;
    TextView toneView;
    TextView writingView;
    TextView meaningView;
    TextView partView;
    TextView countView;

    Random random = new Random();

    private void remember() {
        if (tango != null && tango.id > 0) {
            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            int goal = DataManager.getInstance().getInt("tango_goal", 30);
            if (!isSameDate(now, last)) {
                todayConsecutive = 0;
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setInt("tango_review", review);
                    DataManager.getInstance().setLong("last_time", now.getTime());
                    if (frequency > 0) {
                        frequency--;
                    }
                } else { //学习
                    count++;
                    frequency = Constants.REVIEW_FREQUENCY;
                    DataManager.getInstance().setInt("tango_count", count);
                    DataManager.getInstance().setLong("last_time", now.getTime());
                }
                countView.setText("今日复习：" + review + "\n今日已记：" + count);
            } else if (count >= goal) {
                todayConsecutive++;
                if (todayConsecutive > Constants.TODAY_CONSECUTIVE_REVIEW_MAX) {
                    todayConsecutive = 0;
                    int frequencyMax = DataManager.getInstance().getInt("review_frequency",
                            Constants.REVIEW_FREQUENCY);
                    frequencyMax--;
                    DataManager.getInstance().setInt("review_frequency", frequencyMax);
                }
            }

            int score = Constants.REMEMBER_SCORE - (count + review) / Constants.TIRED_COEFFICIENT;
            score = Math.max(score, Constants.REMEMBER_MIN_SCORE);
            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + score),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new TangoListChangeEvent());
        }
    }

    private void forget() {
        if (tango != null && tango.id > 0) {
            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + Constants.FORGET_SCORE));
            //"LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new TangoListChangeEvent());
        }
    }

    private void rememberForever() {
        if (tango != null && tango.id > 0) {
            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setInt("tango_review", review);
                    DataManager.getInstance().setLong("last_time", now.getTime());
                } else { //学习
                    count++;
                    DataManager.getInstance().setInt("tango_count", count);
                    DataManager.getInstance().setLong("last_time", now.getTime());
                }
                countView.setText("今日复习：" + review + "\n今日已记：" + count);
            }
            frequency = 0;

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + Constants.REMEMBER_FOREVER_SCORE),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new TangoListChangeEvent());
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

        Date last = new Date(DataManager.getInstance().getLong("last_time", 0));
        Date now = new Date();
        count = DataManager.getInstance().getInt("tango_count", 0);
        review = DataManager.getInstance().getInt("tango_review", 0);
        if (!isSameDate(now, last)) {
            count = 0;
            DataManager.getInstance().setInt("tango_count", 0);
            review = 0;
            DataManager.getInstance().setInt("tango_review", 0);

            DataManager.getInstance().setInt("review_frequency", Constants.REVIEW_FREQUENCY);

            DataManager.getInstance().setLong("last_time", now.getTime());
        }
        countView.setText("今日复习：" + review + "\n今日已记：" + count);
    }

    @Override
    protected void setListener(View view) {
        Button remember = (Button) view.findViewById(R.id.btn_remember);
        remember.setOnTouchListener(new View.OnTouchListener() {
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
                            rememberForever();
                            loadNewTango();
                        }
                        break;
                }
                return false;
            }
        });

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remember();
                loadNewTango();
            }
        });

        view.findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forget();
                loadNewTango();
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
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {
        loadNewTango();
    }

    private void loadNewTango() {
        int goal = DataManager.getInstance().getInt("tango_goal", 30);
        boolean reviewFlag = count >= goal;
        Tango temp = TangoManager.getInstance().randomTango(reviewFlag, DataManager.getInstance().getInt("review_frequency",
                Constants.REVIEW_FREQUENCY));
        if (tango != null && temp.id == tango.id) {
            tango = TangoManager.getInstance().nextTango(reviewFlag, DataManager.getInstance().getInt("review_frequency",
                    Constants.REVIEW_FREQUENCY));
        } else {
            tango = temp;
        }

        boolean r = random.nextBoolean();
        if (r) {
            pronunciationView.setText(tango.pronunciation);
            if (tango.tone >= 0) {
                toneView.setText(Constants.TONES[tango.tone]);
            }
            writingView.setText("");
            if (answerTimer != null) {
                answerTimer.stop();
            }
            answerTimer = new Timer() {
                @Override
                public void timer() {
                    writingView.setText(tango.writing);
                    answerTimer.stop();
                }
            };
            answerTimer.start((int) DataManager.getInstance().getFloat("answer_time", 2.5f) * 1000);
        } else {
            writingView.setText(tango.writing);
            pronunciationView.setText("");
            toneView.setText("");
            if (answerTimer != null) {
                answerTimer.stop();
            }
            answerTimer = new Timer() {
                @Override
                public void timer() {
                    pronunciationView.setText(tango.pronunciation);
                    if (tango.tone >= 0) {
                        toneView.setText(Constants.TONES[tango.tone]);
                    }
                    answerTimer.stop();
                }
            };
            answerTimer.start((int) DataManager.getInstance().getFloat("answer_time", 2.5f) * 1000);
        }

        meaningView.setText("");
        partView.setText("");
        if (meaningTimer != null) {
            meaningTimer.stop();
        }
        meaningTimer = new Timer() {
            @Override
            public void timer() {
                if (!tango.partOfSpeech.equals("")) {
                    partView.setText("[" + tango.partOfSpeech + "]");
                }
                meaningView.setText(tango.meaning);
                meaningTimer.stop();
            }
        };
        meaningTimer.start((int) DataManager.getInstance().getFloat("meaning_time", 3.5f) * 1000);
    }

    boolean isSameDate(Date now, Date last) {
        return now.getTime() - last.getTime() < Constants.DAY_TIME
                && now.getDate() == last.getDate();
    }
}
