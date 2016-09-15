package com.xmx.tango.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xmx.tango.R;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoAdapter;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tango.UpdateTangoDialog;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;

import net.gimite.jatts.JapaneseTextToSpeech;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_tango_list)
public class TangoListFragment extends xUtilsFragment {

    @ViewInject(R.id.list_tango)
    ListView tangoList;
    TangoAdapter tangoAdapter;

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        TangoManager.getInstance().updateData();
        tangoAdapter = new TangoAdapter(getContext(), TangoManager.getInstance().getData());
        tangoList.setAdapter(tangoAdapter);

        tangoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tango tango = (Tango) tangoAdapter.getItem(i);
                String writing = tango.writing;
                if (!writing.equals("")) {
                    JapaneseTextToSpeech tts = new JapaneseTextToSpeech(getContext(), null);
                    HashMap<String, String> params = new HashMap<>();
                    params.put(JapaneseTextToSpeech.KEY_PARAM_SPEAKER, "male01");
                    tts.speak(writing, TextToSpeech.QUEUE_FLUSH, params);
                }
            }
        });

        tangoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Tango tango = (Tango) tangoAdapter.getItem(i);

                AlertDialog.Builder builder = new AlertDialog
                        .Builder(getContext());
                builder.setMessage("要编辑该记录吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpdateTangoDialog dialog = new UpdateTangoDialog(getContext(), tango);
                        dialog.show();
                    }
                });
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder = new AlertDialog
                                .Builder(getContext());
                        builder.setMessage("确定要删除吗？");
                        builder.setTitle("提示");
                        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TangoEntityManager.getInstance().deleteById(tango.id);
                                EventBus.getDefault().post(new TangoListChangeEvent());
                            }
                        });
                        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
                builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

        EventBus.getDefault().register(this);
    }

    private void updateTangoList() {
        TangoManager.getInstance().updateData();
        tangoAdapter.updateList(TangoManager.getInstance().getData());
    }

    @Subscribe
    public void onEvent(TangoListChangeEvent event) {
        updateTangoList();
    }
}
