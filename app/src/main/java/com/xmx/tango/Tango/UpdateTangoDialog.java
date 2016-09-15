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

        writingView.setText(tango.writing);
        pronunciationView.setText(tango.pronunciation);
        meaningView.setText(tango.meaning);

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                String pronunciation = pronunciationView.getText().toString();
                String meaning = meaningView.getText().toString();

                if (writing.equals("") && pronunciation.equals("")) {
                    Toast.makeText(getContext(), R.string.add_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                TangoEntityManager.getInstance().updateData(tango.id,
                        "Writing='" + writing + "'",
                        "Pronunciation='" + pronunciation + "'",
                        "Meaning='" + meaning + "'");


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
