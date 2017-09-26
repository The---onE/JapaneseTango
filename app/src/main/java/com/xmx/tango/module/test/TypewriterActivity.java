package com.xmx.tango.module.test;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.xmx.tango.module.operate.TangoOperator;
import com.xmx.tango.module.sentence.SentenceActivity;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.utils.Timer;
import com.xmx.tango.utils.VibratorUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_typewriter)
public class TypewriterActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_typewriter)
    private EditText typewriterEdit;

    @ViewInject(R.id.btn_backspace)
    private Button btnBackspace;
    @ViewInject(R.id.btn_space)
    private Button btnSpace;
    @ViewInject(R.id.btn_masu)
    private Button btnMasu;
    @ViewInject(R.id.btn_keyboard)
    private Button btnKeyboard;
    @ViewInject(R.id.btn_copy)
    private Button btnCopy;
    @ViewInject(R.id.btn_kuromoji)
    private Button btnKuromoji;

    @ViewInject(R.id.layout_keyboard)
    private KeyboardView keyboardView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        keyboardView.init(new InputCallback() {
            @Override
            public void input(String result) {
                inputToEdit(result);
            }
        });
        keyboardView.enable();

        setJapaneseFont();
    }

    @Override
    protected void setListener() {
        typewriterEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                hideSoftInput(view);
            }
        });
        typewriterEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput(view);
            }
        });


        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable s = typewriterEdit.getText();
                int index = typewriterEdit.getSelectionStart();
                if (index > 0) {
                    s.delete(index - 1, index);
                }
                if (DataManager.getInstance().getVibratorStatus()) {
                    VibratorUtil.vibrate(TypewriterActivity.this,
                            TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME);
                }
            }
        });

        btnBackspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                typewriterEdit.getText().clear();
                return true;
            }
        });

        btnSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputToEdit(" ");
            }
        });

        btnMasu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputToEdit("ます");
            }
        });

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(typewriterEdit, 0);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = typewriterEdit.getText().toString();
                if (text.trim().length() > 0) {
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("label", text); //文本型数据 clipData 的构造方法。
                    cmb.setPrimaryClip(clipData);
                    if (DataManager.getInstance().getVibratorStatus()) {
                        VibratorUtil.vibrate(TypewriterActivity.this,
                                TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME);
                    }
                    showToast("已复制到剪切板");
                } else {
                    showToast("请输入内容");
                }
            }
        });

        btnKuromoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentence = typewriterEdit.getText().toString();
                if (sentence.trim().length() > 0) {
                    Intent intent = new Intent(TypewriterActivity.this, SentenceActivity.class);
                    intent.putExtra("sentence", sentence);
                    startActivity(intent);
                } else {
                    showToast("请输入内容");
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
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
        typewriterEdit.setTypeface(tf);
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm
                = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void inputToEdit(String re) {
        int i = typewriterEdit.getSelectionStart();
        Editable s = typewriterEdit.getText();
        s.insert(i, re);
        if (DataManager.getInstance().getVibratorStatus()) {
            VibratorUtil.vibrate(TypewriterActivity.this,
                    TangoConstants.KEYBOARD_INPUT_VIBRATE_TIME);
        }
    }
}
