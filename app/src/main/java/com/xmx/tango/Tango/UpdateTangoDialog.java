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
 * Created by The_onE on 2016/9/15.
 */
public class UpdateTangoDialog extends Dialog {
    Tango tango;

    EditText writingView;
    EditText pronunciationView;
    EditText meaningView;
    EditText toneView;
    EditText partOfSpeechView;
    EditText typeView;

    public UpdateTangoDialog(Context context, Tango tango) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        this.tango = tango;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_tango);

        writingView = (EditText) findViewById(R.id.edit_writing);
        pronunciationView = (EditText) findViewById(R.id.edit_pronunciation);
        meaningView = (EditText) findViewById(R.id.edit_meaning);
        toneView = (EditText) findViewById(R.id.edit_tone);
        partOfSpeechView = (EditText) findViewById(R.id.edit_part_of_speech);
        typeView = (EditText) findViewById(R.id.edit_type);

        writingView.setText(tango.writing);
        pronunciationView.setText(tango.pronunciation);
        meaningView.setText(tango.meaning);
        toneView.setText("" + tango.tone);
        partOfSpeechView.setText(tango.partOfSpeech);
        typeView.setText(tango.type);

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                String pronunciation = pronunciationView.getText().toString();
                String meaning = meaningView.getText().toString();
                String toneStr = toneView.getText().toString();
                String partOfSpeech = partOfSpeechView.getText().toString();
                String type = typeView.getText().toString();

                if (writing.equals("") && pronunciation.equals("")) {
                    Toast.makeText(getContext(), R.string.add_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                int tone = -1;
                if (!toneStr.equals("")) {
                    try {
                        tone = Integer.parseInt(toneStr);
                    } catch (Exception e) {
                        Toast.makeText(getContext(),"音调格式不合法", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                TangoEntityManager.getInstance().updateData(tango.id,
                        "Writing='" + writing + "'",
                        "Pronunciation='" + pronunciation + "'",
                        "Meaning='" + meaning + "'",
                        "Tone=" + tone,
                        "PartOfSpeech='" + partOfSpeech + "'",
                        "Type='" + type + "'");


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
