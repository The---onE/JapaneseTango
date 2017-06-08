package com.xmx.tango.module.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.keyboard.InputCallback;
import com.xmx.tango.module.keyboard.KeyboardView;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.tango.TangoOperator;
import com.xmx.tango.utils.Timer;
import com.xmx.tango.utils.VibratorUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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

    @ViewInject(R.id.layout_keyboard)
    private KeyboardView keyboardView;

    Tango tango;
    Tango prevTango;
    boolean hintFlag = false;
    boolean writingFlag = false;
    boolean enableFlag = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        keyboardView.init(new InputCallback() {
            @Override
            public void input(String result) {
                inputToEdit(result);
            }
        });

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
                    if (DataManager.getInstance().getVibratorStatus()) {
                        VibratorUtil.vibrate(TestActivity.this,
                                TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME);
                    }
                    checkAnswer();
                }
            }
        });

        btnBackspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (enableFlag) {
                    testEdit.getText().clear();
                }
                return true;
            }
        });

        btnSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    inputToEdit(" ");
                }
            }
        });

        btnMasu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enableFlag) {
                    inputToEdit("ます");
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
        length = measureWidth(meaningView);
        while (length > width) {
            textSize -= 1;
            meaningView.setTextSize(textSize);
            length = measureWidth(meaningView);
        }

        textSize = TangoConstants.DEFAULT_TEST_WRITING_TEXT_SIZE;
        writingView.setText(tango.writing);
        writingView.setTextSize(textSize);
        writingView.setVisibility(View.INVISIBLE);
        length = measureWidth(writingView);
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
        keyboardView.enable();
    }

    private void showAnswer() {
        TangoOperator.getInstance().wrong(tango);
        testEdit.setText(tango.pronunciation);
        testEdit.setEnabled(false);
        if (!writingFlag) {
            showTextView(writingView);
        }
        enableFlag = false;
        keyboardView.disable();
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
            keyboardView.disable();
            if (DataManager.getInstance().getVibratorStatus()) {
                VibratorUtil.vibrate(TestActivity.this,
                        TangoConstants.TEST_RIGHT_VIBRATE_TIME);
            }
            new Timer() {
                @Override
                public void timer() {
                    loadNewTango();
                }
            }.start(TangoConstants.NEW_TANGO_DELAY, true);
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

    private void inputToEdit(String re) {
        int i = testEdit.getSelectionStart();
        Editable s = testEdit.getText();
        s.insert(i, re);
        if (DataManager.getInstance().getVibratorStatus()) {
            VibratorUtil.vibrate(TestActivity.this,
                    TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME);
        }
        checkAnswer();
    }
}
