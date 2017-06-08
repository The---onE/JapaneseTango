package com.xmx.tango.module.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.xmx.tango.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The_onE on 2017/6/7.
 */

public class KeyboardView extends GridLayout {

    private Context mContext;
    private Map<Button, Integer> buttons = new HashMap<>();
    private FlickView flickView;
    InputCallback inputCallback;

    private int kanaSize = 30;
    private int switchSize = 24;

    private int voicing = 0;
    private int kata = 0;

    private int startBase;
    private float startX;
    private float startY;

    boolean enableFlag = false;
    boolean initFlag = false;

    public KeyboardView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_keyboard, this);
        mContext = context;

        Spec rowSpec, columnSpec;
        LayoutParams layoutParams;

        for (int i = 1; i <= KeyboardConstants.VOICING_LINES; ++i) {
            Button button = new Button(context);
            int base = (i - 1) * 5;
            button.setText(KeyboardConstants.kanaArray[base]);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, kanaSize);
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!initFlag) {
                        Toast.makeText(mContext, "You must initialize KeyboardView first!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
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
                                    inputCallback.input(result);
                                }
                                break;
                        }
                        return true;
                    }
                    return false;
                }
            });

            int row = (i - 1) / 3;
            rowSpec = GridLayout.spec(row);
            int column;
            if (i != KeyboardConstants.VOICING_LINES) {
                column = (i - 1) % 3;
            } else {
                column = 1;
            }
            columnSpec = GridLayout.spec(column);
            layoutParams = new LayoutParams(rowSpec, columnSpec);
            this.addView(button, layoutParams);
            buttons.put(button, i);
        }

        Button btnVoicing = new Button(context);
        btnVoicing.setText("清/浊");
        btnVoicing.setTextSize(TypedValue.COMPLEX_UNIT_SP, switchSize);
        btnVoicing.setBackgroundColor(Color.TRANSPARENT);
        btnVoicing.setOnClickListener(new OnClickListener() {
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
        rowSpec = GridLayout.spec(3);
        columnSpec = GridLayout.spec(0);
        layoutParams = new LayoutParams(rowSpec, columnSpec);
        this.addView(btnVoicing, layoutParams);

        Button btnKata = new Button(context);
        btnKata.setText("平/片");
        btnKata.setTextSize(TypedValue.COMPLEX_UNIT_SP, switchSize);
        btnKata.setBackgroundColor(Color.TRANSPARENT);
        btnKata.setOnClickListener(new OnClickListener() {
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
        rowSpec = GridLayout.spec(3);
        columnSpec = GridLayout.spec(2);
        layoutParams = new LayoutParams(rowSpec, columnSpec);
        this.addView(btnKata, layoutParams);
    }

    public void init(InputCallback callback) {
        initFlag = true;
        inputCallback = callback;

        Context c = mContext;
        Activity parent = null;
        while (c instanceof ContextWrapper) {
            if (mContext instanceof Activity) {
                parent = (Activity) mContext;
            }
            c = ((ContextWrapper) mContext).getBaseContext();
        }
        if (parent != null) {
            flickView = new FlickView(parent);
            parent.addContentView(flickView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
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

    public void enable() {
        enableFlag = true;
    }

    public void disable() {
        enableFlag = false;
    }
}
