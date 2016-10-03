package com.xmx.tango.Tango;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xmx.tango.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by The_onE on 2016/9/21.
 */
public class SearchTangoDialog extends Dialog {

    EditText writingView;
    EditText pronunciationView;
    EditText meaningView;
    EditText partOfSpeechView;
    EditText typeView;

    public SearchTangoDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_tango);

        writingView = (EditText) findViewById(R.id.edit_writing);
        pronunciationView = (EditText) findViewById(R.id.edit_pronunciation);
        meaningView = (EditText) findViewById(R.id.edit_meaning);
        partOfSpeechView = (EditText) findViewById(R.id.edit_part_of_speech);
        typeView = (EditText) findViewById(R.id.edit_type);

        writingView.setText(TangoManager.getInstance().writing);
        pronunciationView.setText(TangoManager.getInstance().pronunciation);
        meaningView.setText(TangoManager.getInstance().meaning);
        partOfSpeechView.setText(TangoManager.getInstance().partOfSpeech);
        typeView.setText(TangoManager.getInstance().type);

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                String pronunciation = pronunciationView.getText().toString();
                String meaning = meaningView.getText().toString();
                String partOfSpeech = partOfSpeechView.getText().toString();
                String type = typeView.getText().toString();

                TangoManager.getInstance().writing = writing;
                TangoManager.getInstance().pronunciation = pronunciation;
                TangoManager.getInstance().meaning = meaning;
                TangoManager.getInstance().partOfSpeech = partOfSpeech;
                TangoManager.getInstance().type = type;

                EventBus.getDefault().post(new TangoListChangeEvent());
                dismiss();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}