package com.xmx.tango.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_add_tango)
public class AddTangoFragment extends xUtilsFragment {

    @ViewInject(R.id.edit_writing)
    EditText writingView;

    @ViewInject(R.id.edit_pronunciation)
    EditText pronunciationView;

    @ViewInject(R.id.edit_meaning)
    EditText meaningView;

    @Event(value = R.id.btn_add_tango)
    private void onAddTangoClick(View view) {
        Tango entity = new Tango();

        String writing = writingView.getText().toString();
        entity.writing = writing;

        String pronunciation = pronunciationView.getText().toString();
        entity.pronunciation = pronunciation;

        String meaning = meaningView.getText().toString();
        entity.meaning = meaning;

        if (writing.equals("") && pronunciation.equals("")) {
            showToast(R.string.add_error);
            return;
        }

        entity.addTime = new Date();
        TangoEntityManager.getInstance().insertData(entity);

        writingView.setText("");
        pronunciationView.setText("");
        meaningView.setText("");

        showToast(R.string.add_success);

        EventBus.getDefault().post(new TangoListChangeEvent());
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
