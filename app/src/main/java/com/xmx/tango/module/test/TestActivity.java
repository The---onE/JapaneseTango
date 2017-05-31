package com.xmx.tango.module.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_test)
public class TestActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_test)
    private EditText testEdit;

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

    Map<Button, Integer> buttons = new HashMap<>();
    FlickView flickView;

    private int voicing = 0;
    private int kata = 0;

    private String[] kanaArray = new String[]{
            "あ", "い", "う", "え", "お",
            "か", "き", "く", "け", "こ",
            "さ", "し", "す", "せ", "そ",
            "た", "ち", "つ", "て", "と",
            "な", "に", "ぬ", "ね", "の",
            "は", "ひ", "ふ", "へ", "ほ",
            "ま", "み", "む", "め", "も",
            "や", "い", "ゆ", "え", "よ",
            "ら", "り", "る", "れ", "ろ",
            "わ", "を", "ん", "々", " ",

            "ぁ", "ぃ", "ぅ", "ぇ", "ぉ",
            "が", "ぎ", "ぐ", "げ", "ご",
            "ざ", "じ", "ず", "ぜ", "ぞ",
            "だ", "ぢ", "づ", "で", "ど",
            "な", "に", "ぬ", "ね", "の",
            "ば", "び", "ぶ", "べ", "ぼ",
            "ま", "み", "む", "め", "も",
            "ゃ", "ぃ", "ゅ", "ぇ", "ょ",
            "ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
            "わ", "ゐ", "っ", "ゑ", " ",

            "ア", "イ", "ウ", "エ", "オ",
            "カ", "キ", "ク", "ケ", "コ",
            "サ", "シ", "ス", "セ", "ソ",
            "タ", "チ", "ツ", "テ", "ト",
            "ナ", "ニ", "ヌ", "ネ", "ノ",
            "ハ", "ヒ", "フ", "ヘ", "ホ",
            "マ", "ミ", "ム", "メ", "モ",
            "ヤ", "イ", "ユ", "エ", "ヨ",
            "ラ", "リ", "ル", "レ", "ロ",
            "ワ", "ヲ", "ン", "ー", " ",

            "ァ", "ィ", "ゥ", "ェ", "ォ",
            "ガ", "ギ", "グ", "ゲ", "ゴ",
            "ザ", "ジ", "ズ", "ゼ", "ゾ",
            "ダ", "ヂ", "ヅ", "デ", "ド",
            "ナ", "ニ", "ヴ", "ネ", "ノ",
            "バ", "ビ", "ブ", "ベ", "ボ",
            "マ", "ミ", "ム", "メ", "モ",
            "ャ", "ヵ", "ュ", "ヶ", "ョ",
            "パ", "ピ", "プ", "ペ", "ポ",
            "わ", "ヰ", "ッ", "ヱ", " ",
    };

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
    }

    @Override
    protected void setListener() {
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
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            int index = buttons.get(view);
                            index += voicing * 10;
                            index += kata * 20;
                            int base = (index - 1) * 5;
                            float x = motionEvent.getRawX();
                            int statusHeight = 0;
                            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                            if (resourceId > 0) {
                                statusHeight = getResources().getDimensionPixelSize(resourceId);
                            }
                            float y = motionEvent.getRawY() - statusHeight;
                            flickView.show(x, y, new String[]{
                                    kanaArray[base],
                                    kanaArray[base + 1],
                                    kanaArray[base + 2],
                                    kanaArray[base + 3],
                                    kanaArray[base + 4]
                            });
                            break;
                        case MotionEvent.ACTION_UP:
                            flickView.remove();
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    private void updateButton() {
        for (Button b : buttons.keySet()) {
            int index = buttons.get(b);
            index += voicing * 10;
            index += kata * 20;
            int base = (index - 1) * 5;
            b.setText(kanaArray[base]);
        }
    }
}
