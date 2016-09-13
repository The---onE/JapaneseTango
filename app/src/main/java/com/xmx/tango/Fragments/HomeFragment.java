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

import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tools.FragmentBase.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    Tango tango;

    TextView pronunciation;
    TextView writing;
    TextView meaning;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initView(View view) {
        pronunciation = (TextView) view.findViewById(R.id.tv_tango_pronunciation);
        writing = (TextView) view.findViewById(R.id.tv_tango_writing);
        meaning = (TextView) view.findViewById(R.id.tv_tango_meaning);
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
                            loadNewTango();
                            showToast(R.string.remember_forever);
                        }
                        break;
                }
                return false;
            }
        });

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewTango();
                showToast(R.string.remember);
            }
        });

        view.findViewById(R.id.btn_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewTango();
                showToast(R.string.forget);
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {
        loadNewTango();
    }

    private void loadNewTango() {
        tango = TangoManager.getInstance().randomTango();

        pronunciation.setText(tango.pronunciation);
        writing.setText(tango.writing);
        meaning.setText(tango.meaning);
    }
}
