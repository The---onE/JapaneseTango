package com.xmx.tango.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xmx.tango.Constants;
import com.xmx.tango.R;
import com.xmx.tango.Tango.OperateTangoEvent;
import com.xmx.tango.Tango.SearchTangoDialog;
import com.xmx.tango.Tango.SpeakTangoManager;
import com.xmx.tango.Tango.Tango;
import com.xmx.tango.Tango.TangoAdapter;
import com.xmx.tango.Tango.TangoEntityManager;
import com.xmx.tango.Tango.TangoListChangeEvent;
import com.xmx.tango.Tango.TangoManager;
import com.xmx.tango.Tango.UpdateTangoDialog;
import com.xmx.tango.Tools.FragmentBase.xUtilsFragment;
import com.xmx.tango.Tools.Utils.CSVUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_tango_list)
public class TangoListFragment extends xUtilsFragment {

    @ViewInject(R.id.list_tango)
    ListView tangoList;
    TangoAdapter tangoAdapter;

    @Event(value = R.id.btn_search)
    private void onSearchClick(View view) {
        SearchTangoDialog dialog = new SearchTangoDialog(getContext());
        dialog.show();
    }

    @Event(value = R.id.btn_export)
    private void onExportClick(View view) {
        List<Tango> list = TangoManager.getInstance().getData();
        String dir = android.os.Environment.getExternalStorageDirectory() + Constants.FILE_DIR;
        String filename = "/export.csv";
        Collection<String> items = new ArrayList<>();
        for (Tango tango : list) {
            String res = tango.writing + "," + tango.pronunciation + "," + tango.meaning + ","
                    + tango.tone + "," + tango.partOfSpeech + ",";
            items.add(res);
        }
        if (CSVUtil.toCSV(items, dir + filename, "UTF-8")) {
            showToast("成功导出至:" + dir + filename);
        } else {
            showToast("导出失败");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        //TangoManager.getInstance().updateData(); //在SplashActivity中调用
        tangoAdapter = new TangoAdapter(getContext(), TangoManager.getInstance().getData());
        tangoList.setAdapter(tangoAdapter);

        tangoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tango tango = (Tango) tangoAdapter.getItem(i);
                String writing = tango.writing;
                if (!writing.equals("")) {
                    SpeakTangoManager.getInstance().speak(writing);
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

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void updateTangoList() {
        TangoManager.getInstance().updateData();
        tangoAdapter.updateList(TangoManager.getInstance().getData());
    }

    @Subscribe
    public void onEvent(TangoListChangeEvent event) {
        updateTangoList();
    }

    @Subscribe
    public void onEvent(OperateTangoEvent event) {
    }
}
