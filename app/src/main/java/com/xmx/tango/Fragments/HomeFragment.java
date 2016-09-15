package com.xmx.tango.Fragments;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tools.Data.DataManager;
import com.xmx.tango.Tools.FragmentBase.BaseFragment;
import com.xmx.tango.Tools.Timer;

import net.gimite.jatts.JapaneseTextToSpeech;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    static final int REMEMBER_SCORE = 5;
    static final int TIRED_COEFFICIENT = 25;
    static final int REMEMBER_MIN_SCORE = 2;
    static final int FORGET_SCORE = -2;
    static final int REMEMBER_FOREVER_SCORE = 32;

    Tango tango;
    int count;

    long answerTime = 2500;
    Timer answerTimer;
    long meaningTime = 3500;
    Timer meaningTimer;

    TextView pronunciationView;
    TextView writingView;
    TextView meaningView;
    TextView countView;

    Random random = new Random();

    private void remember() {
        if (tango != null) {
            Date last = tango.lastTime;
            Date now = new Date();
            if (!isSameDate(now, last)) {
                count++;
                DataManager.getInstance().setInt("tango_count", count);
                DataManager.getInstance().setLong("last_time", now.getTime());
                countView.setText("今日已记：" + count);
            }

            int score = REMEMBER_SCORE - count / TIRED_COEFFICIENT;
            score = Math.max(score, REMEMBER_MIN_SCORE);
            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + score),
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new TangoListChangeEvent());
        }
    }

    private void forget() {
        if (tango != null) {
            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + FORGET_SCORE));
            //"LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new TangoListChangeEvent());
        }
    }

    private void rememberForever() {
        if (tango != null) {

            Date last = tango.lastTime;
            Date now = new Date();
            if (!isSameDate(now, last)) {
                count++;
                DataManager.getInstance().setInt("tango_count", count);
                DataManager.getInstance().setLong("last_time", now.getTime());
                countView.setText("今日已记：" + count);
            }

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + REMEMBER_FOREVER_SCORE),
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
        writingView = (TextView) view.findViewById(R.id.tv_tango_writing);
        meaningView = (TextView) view.findViewById(R.id.tv_tango_meaning);
        countView = (TextView) view.findViewById(R.id.tv_tango_count);

        Date last = new Date(DataManager.getInstance().getLong("last_time", 0));
        Date now = new Date();
        count = DataManager.getInstance().getInt("tango_count", 0);
        if (!isSameDate(now, last)) {
            count = 0;
            DataManager.getInstance().setInt("tango_count", 0);
            DataManager.getInstance().setLong("last_time", now.getTime());
        }
        countView.setText("今日已记：" + count);
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
                    JapaneseTextToSpeech tts = new JapaneseTextToSpeech(getContext(), null);
                    HashMap<String, String> params = new HashMap<>();
                    params.put(JapaneseTextToSpeech.KEY_PARAM_SPEAKER, "male01");
                    tts.speak(writing, TextToSpeech.QUEUE_FLUSH, params);
                }
            }
        });

        pronunciationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pronunciation = pronunciationView.getText().toString();
                if (!pronunciation.equals("")) {
                    JapaneseTextToSpeech tts = new JapaneseTextToSpeech(getContext(), null);
                    HashMap<String, String> params = new HashMap<>();
                    params.put(JapaneseTextToSpeech.KEY_PARAM_SPEAKER, "male01");
                    tts.speak(pronunciation, TextToSpeech.QUEUE_FLUSH, params);
                }
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {
        loadNewTango();
    }

    private void loadNewTango() {
        Tango temp = TangoManager.getInstance().randomTango();
        if (tango != null && temp.id == tango.id) {
            tango = TangoManager.getInstance().nextTango();
        } else {
            tango = temp;
        }

        boolean r = random.nextBoolean();
        if (r) {
            pronunciationView.setText(tango.pronunciation);
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
            answerTimer.start(answerTime);
        } else {
            writingView.setText(tango.writing);
            pronunciationView.setText("");
            if (answerTimer != null) {
                answerTimer.stop();
            }
            answerTimer = new Timer() {
                @Override
                public void timer() {
                    pronunciationView.setText(tango.pronunciation);
                    answerTimer.stop();
                }
            };
            answerTimer.start(answerTime);
        }

        meaningView.setText("");
        if (meaningTimer != null) {
            meaningTimer.stop();
        }
        meaningTimer = new Timer() {
            @Override
            public void timer() {
                meaningView.setText(tango.meaning);
                meaningTimer.stop();
            }
        };
        meaningTimer.start(meaningTime);
    }

    boolean isSameDate(Date now, Date last) {
        return now.getTime() - last.getTime() < Constants.DAY_TIME
                && now.getDate() == last.getDate();
    }
}
