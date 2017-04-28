package com.xmx.tango.module.tango;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_add_tango)
public class AddTangoActivity extends BaseTempActivity {

    @ViewInject(R.id.edit_writing)
    EditText writingView;

    @ViewInject(R.id.edit_pronunciation)
    EditText pronunciationView;

    @ViewInject(R.id.edit_meaning)
    EditText meaningView;

    @ViewInject(R.id.edit_tone)
    EditText toneView;

    @ViewInject(R.id.edit_part_of_speech)
    EditText partOfSpeechView;

    @ViewInject(R.id.edit_type)
    EditText typeView;

    @Event(value = R.id.btn_add_tango)
    private void onAddTangoClick(View view) {
        Tango entity = new Tango();

        String writing = writingView.getText().toString().trim();
        entity.writing = writing;

        String pronunciation = pronunciationView.getText().toString().trim();
        entity.pronunciation = pronunciation;

        String meaning = meaningView.getText().toString().trim();
        entity.meaning = meaning;

        String toneStr = toneView.getText().toString().trim();
        int tone = -1;
        if (!toneStr.equals("")) {
            try {
                tone = Integer.parseInt(toneStr);
            } catch (Exception e) {
                showToast("音调格式不合法");
                return;
            }
        }
        entity.tone = tone;

        String partOfSpeech = partOfSpeechView.getText().toString().trim();
        entity.partOfSpeech = partOfSpeech;

        String type = typeView.getText().toString().trim();
        entity.type = type;

        if (writing.equals("") && pronunciation.equals("")) {
            showToast(R.string.add_error);
            return;
        }

        entity.addTime = new Date();
        TangoEntityManager.getInstance().insertData(entity);

        writingView.setText("");
        pronunciationView.setText("");
        meaningView.setText("");
        toneView.setText("");
        partOfSpeechView.setText("");
        typeView.setText("");

        showToast(R.string.add_success);

        EventBus.getDefault().post(new TangoListChangeEvent());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
