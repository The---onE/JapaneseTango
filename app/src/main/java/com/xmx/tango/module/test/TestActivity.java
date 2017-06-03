package com.xmx.tango.module.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.keyboard.FlickView;
import com.xmx.tango.module.keyboard.KeyboardConstants;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.tango.TangoOperator;
import com.xmx.tango.utils.Timer;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_test)
public class TestActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_test)
    private EditText testEdit;
    @ViewInject(R.id.text_writing)
    private TextView writingView;
    @ViewInject(R.id.text_meaning)
    private TextView meaningView;

    @ViewInject(R.id.btn_11)
    private Button btn11;
    @ViewInject(R.id.btn_12)
    private Button btn12;
    @ViewInject(R.id.btn_13)
    private Button btn13;
    @ViewInject(R.id.btn_21)
    private Button btn21;
    @ViewInject(R.id.btn_22)
    private Button btn22;
    @ViewInject(R.id.btn_23)
    private Button btn23;
    @ViewInject(R.id.btn_31)
    private Button btn31;
    @ViewInject(R.id.btn_32)
    private Button btn32;
    @ViewInject(R.id.btn_33)
    private Button btn33;
    @ViewInject(R.id.btn_41)
    private Button btn41;
    @ViewInject(R.id.btn_42)
    private Button btn42;
    @ViewInject(R.id.btn_43)
    private Button btn43;

    @ViewInject(R.id.btn_answer)
    private Button btnAnswer;
    @ViewInject(R.id.btn_backspace)
    private Button btnBackspace;
    @ViewInject(R.id.btn_hint)
    private Button btnHint;
    @ViewInject(R.id.btn_space)
    private Button btnSpace;
    @ViewInject(R.id.btn_masu)
    private Button btnMasu;
    @ViewInject(R.id.btn_keyboard)
    private Button btnKeyboard;

    Map<Button, Integer> buttons = new HashMap<>();
    FlickView flickView;

    private int voicing = 0;
    private int kata = 0;

    private int startBase;
    private float startX;
    private float startY;

    Tango tango;
    Tango prevTango;
    boolean hintFlag = false;
    boolean writingFlag = false;
    boolean enableFlag = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        buttons.put(btn11, 1);
        buttons.put(btn12, 2);
        buttons.put(btn13, 3);
        buttons.put(btn21, 4);
        buttons.put(btn22, 5);
        buttons.put(btn23, 6);
        buttons.put(btn31, 7);
        buttons.put(btn32, 8);
        buttons.put(btn33, 9);
        buttons.put(btn42, 10);

        flickView = new FlickView(this);
        addContentView(flickView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        updateButton();
        setJapaneseFont();
    }

    @Override
    protected void setListener() {
        testEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                hideSoftInput(view);
            }
        });
        testEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput(view);
            }
        });

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    showAnswer();
                }
            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    showWriting();
                }
            }
        });

        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    Editable s = testEdit.getText();
                    int index = testEdit.getSelectionStart();
                    if (index > 0) {
                        s.delete(index - 1, index);
                    }
                    checkAnswer();
                }
            }
        });

        btnBackspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (enableFlag) {
                    Editable s = testEdit.getText();
                    s.clear();
                }
                return true;
            }
        });

        btnSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    int i = testEdit.getSelectionStart();
                    Editable s = testEdit.getText();
                    s.insert(i, " ");
                    checkAnswer();
                }
            }
        });

        btnMasu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    int i = testEdit.getSelectionStart();
                    Editable s = testEdit.getText();
                    s.insert(i, "ます");
                    checkAnswer();
                }
            }
        });

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(testEdit, 0);
            }
        });

        btn41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voicing == 0) {
                    voicing = 1;
                } else {
                    voicing = 0;
                }
                updateButton();
            }
        });

        btn43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kata == 0) {
                    kata = 1;
                } else {
                    kata = 0;
                }
                updateButton();
            }
        });

        for (Button b : buttons.keySet()) {
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (enableFlag) {
                        int statusHeight = 0;
                        int resourceId;
                        float x, y;
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                int index = buttons.get(view);
                                index += voicing * KeyboardConstants.VOICING_LINES;
                                index += kata * KeyboardConstants.KATA_LINES;
                                int base = (index - 1) * 5;
                                x = motionEvent.getRawX();
                                resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                                if (resourceId > 0) {
                                    statusHeight = getResources().getDimensionPixelSize(resourceId);
                                }
                                y = motionEvent.getRawY() - statusHeight;
                                flickView.show(x, y, new String[]{
                                        KeyboardConstants.kanaArray[base],
                                        KeyboardConstants.kanaArray[base + 1],
                                        KeyboardConstants.kanaArray[base + 2],
                                        KeyboardConstants.kanaArray[base + 3],
                                        KeyboardConstants.kanaArray[base + 4]
                                });

                                startBase = base;
                                startX = x;
                                startY = y;
                                break;
                            case MotionEvent.ACTION_UP:
                                flickView.remove();
                                x = motionEvent.getRawX();
                                resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                                if (resourceId > 0) {
                                    statusHeight = getResources().getDimensionPixelSize(resourceId);
                                }
                                y = motionEvent.getRawY() - statusHeight;
                                String result = null;
                                if (x < startX - FlickView.HALF_WIDTH) {
                                    if (startY - FlickView.HALF_HEIGHT < y &&
                                            y < startY + FlickView.HALF_HEIGHT) {
                                        result = KeyboardConstants.kanaArray[startBase + 1];
                                    }
                                } else if (x > startX + FlickView.HALF_WIDTH) {
                                    if (startY - FlickView.HALF_HEIGHT < y &&
                                            y < startY + FlickView.HALF_HEIGHT) {
                                        result = KeyboardConstants.kanaArray[startBase + 3];
                                    }
                                } else {
                                    if (y < startY - FlickView.HALF_HEIGHT) {
                                        result = KeyboardConstants.kanaArray[startBase + 2];
                                    } else if (y > startY + FlickView.HALF_HEIGHT) {
                                        result = KeyboardConstants.kanaArray[startBase + 4];
                                    } else {
                                        result = KeyboardConstants.kanaArray[startBase];
                                    }
                                }
                                if (result != null && !result.trim().equals("")) {
                                    int i = testEdit.getSelectionStart();
                                    Editable s = testEdit.getText();
                                    s.insert(i, result);
                                    checkAnswer();
                                }
                                break;
                        }
                    }
                    return false;
                }
            });
        }
    }


    private void showTextView(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f); // 渐入效果
        animator.setDuration(300);
        animator.start();
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadNewTango();
    }

    private void loadNewTango() {
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int textSize;
        float length;

        prevTango = tango;
        tango = TangoManager.getInstance().randomTango(true,
                DataManager.getInstance().getReviewFrequency(), prevTango, false);
        int count = 0;
        while (tango.meaning.trim().equals("")) {
            tango = TangoManager.getInstance().randomTango(true,
                    DataManager.getInstance().getReviewFrequency(), prevTango, false);
            count++;
            if (count > 100) {
                showToast("没有符合条件的测试");
                finish();
            }
        }

        textSize = TangoConstants.DEFAULT_TEST_MEANING_TEXT_SIZE;
        if (!tango.partOfSpeech.equals("")) {
            meaningView.setText("[" + tango.partOfSpeech + "]" + tango.meaning);
        } else {
            meaningView.setText(tango.meaning);
        }
        meaningView.setTextSize(textSize);
        meaningView.setVisibility(View.INVISIBLE);
        length= measureWidth(meaningView);
        while (length > width) {
            textSize -= 1;
            meaningView.setTextSize(textSize);
            length = measureWidth(meaningView);
        }

        textSize = TangoConstants.DEFAULT_TEST_WRITING_TEXT_SIZE;
        writingView.setText(tango.writing);
        writingView.setTextSize(textSize);
        writingView.setVisibility(View.INVISIBLE);
        length= measureWidth(writingView);
        while (length > width) {
            textSize -= 1;
            writingView.setTextSize(textSize);
            length = measureWidth(writingView);
        }

        showTextView(meaningView);
        hintFlag = false;
        writingFlag = false;
        testEdit.setText("");
        testEdit.setEnabled(true);
        enableFlag = true;
    }

    private void showAnswer() {
        TangoOperator.getInstance().wrong(tango);
        testEdit.setText(tango.pronunciation);
        testEdit.setEnabled(false);
        if (!writingFlag) {
            showTextView(writingView);
        }
        enableFlag = false;
        new Timer() {
            @Override
            public void timer() {
                loadNewTango();
            }
        }.start(TangoConstants.SHOW_ANSWER_DELAY, true);
    }

    private void showWriting() {
        if (!writingFlag) {
            showTextView(writingView);
            writingFlag = true;
        }
        hintFlag = true;
    }

    private void checkAnswer() {
        if (testEdit.getText().toString().equals(tango.pronunciation)) {
            if (!hintFlag) {
                TangoOperator.getInstance().rightWithoutHint(tango);
            } else {
                TangoOperator.getInstance().rightWithHint(tango);
            }
            if (!writingFlag) {
                showTextView(writingView);
            }
            testEdit.setEnabled(false);
            enableFlag = false;
            new Timer() {
                @Override
                public void timer() {
                    loadNewTango();
                }
            }.start(TangoConstants.NEW_TANGO_DELAY, true);
        }
    }

    private void updateButton() {
        for (Button b : buttons.keySet()) {
            int index = buttons.get(b);
            index += voicing * KeyboardConstants.VOICING_LINES;
            index += kata * KeyboardConstants.KATA_LINES;
            int base = (index - 1) * 5;
            b.setText(KeyboardConstants.kanaArray[base]);
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
        testEdit.setTypeface(tf);
        writingView.setTypeface(tf);
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm
                = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private float measureWidth(TextView textView) {
        return textView.getPaint().measureText(textView.getText().toString());
    }
}
