package com.xmx.tango.module.crud;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.xmx.tango.base.dialog.BaseDialog;
import com.xmx.tango.core.Constants;
import com.xmx.tango.R;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by The_onE on 2016/9/15.
 */
public class UpdateTangoDialog extends BaseDialog {
    Tango tango;

    EditText writingView;
    EditText pronunciationView;
    EditText meaningView;
    EditText toneView;
    EditText partOfSpeechView;
    EditText typeView;

    TextView idView;
    TextView scoreView;

    public void initDialog(Context context, Tango tango) {
        super.initDialog(context);
        this.tango = tango;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_update_tango, container);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        writingView = (EditText) view.findViewById(R.id.edit_writing);
        pronunciationView = (EditText) view.findViewById(R.id.edit_pronunciation);
        meaningView = (EditText) view.findViewById(R.id.edit_meaning);
        toneView = (EditText) view.findViewById(R.id.edit_tone);
        partOfSpeechView = (EditText) view.findViewById(R.id.edit_part_of_speech);
        typeView = (EditText) view.findViewById(R.id.edit_type);
        idView = (TextView) view.findViewById(R.id.edit_id);
        scoreView = (TextView) view.findViewById(R.id.edit_score);

        writingView.setText(tango.writing);
        pronunciationView.setText(tango.pronunciation);
        meaningView.setText(tango.meaning);
        toneView.setText("" + tango.tone);
        partOfSpeechView.setText(tango.partOfSpeech);
        typeView.setText(tango.type);

        if (Constants.DEBUG_MODE) {
            idView.setText("" + tango.id);
            scoreView.setText("" + tango.score);
        } else {
            idView.setVisibility(View.GONE);
            scoreView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener(View view) {
        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writing = writingView.getText().toString();
                String pronunciation = pronunciationView.getText().toString();
                String meaning = meaningView.getText().toString();
                String toneStr = toneView.getText().toString();
                String partOfSpeech = partOfSpeechView.getText().toString();
                String type = typeView.getText().toString();

                if (writing.equals("") && pronunciation.equals("")) {
                    showToast(R.string.add_error);
                    return;
                }

                int tone = -1;
                if (!toneStr.equals("")) {
                    try {
                        tone = Integer.parseInt(toneStr);
                    } catch (Exception e) {
                        showToast("音调格式不合法");
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

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {

    }
}
