package com.xmx.tango.core.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xmx.tango.core.Constants;
import com.xmx.tango.module.importtango.ImportFileActivity;
import com.xmx.tango.module.importtango.ImportTangoActivity;
import com.xmx.tango.R;
import com.xmx.tango.module.tango.AddTangoActivity;
import com.xmx.tango.module.tango.ChooseTangoEvent;
import com.xmx.tango.module.tango.JapaneseFontChangeEvent;
import com.xmx.tango.module.tango.OperateTangoEvent;
import com.xmx.tango.module.tango.SearchTangoDialog;
import com.xmx.tango.module.tango.SpeakTangoManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoAdapter;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.tango.TangoListChangeEvent;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.tango.UpdateTangoDialog;
import com.xmx.tango.module.tango.VerbDialog;
import com.xmx.tango.base.fragment.xUtilsFragment;
import com.xmx.tango.utils.CSVUtil;
import com.xmx.tango.utils.StrUtil;

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

    @ViewInject(R.id.tv_count)
    TextView countView;

    @ViewInject(R.id.list_tango)
    ListView tangoList;
    TangoAdapter tangoAdapter;

    boolean itemDoubleFlag = false;

    @Event(value = R.id.btn_search)
    private void onSearchClick(View view) {
        SearchTangoDialog dialog = new SearchTangoDialog();
        dialog.initDialog(getContext());
        dialog.show(getActivity().getFragmentManager(), "SEARCH_TANGO");
    }

    @Event(value = R.id.btn_operation)
    private void onExportClick(View view) {
        String[] items = new String[]{"添加", "导出", "导入", "删除"};
        new AlertDialog.Builder(getContext())
                .setTitle("操作")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                addTango();
                                break;
                            case 1:
                                exportTango();
                                break;
                            case 2:
                                importTango();
                                break;
                            case 3:
                                deleteTango();
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void addTango() {
        startActivity(AddTangoActivity.class);
    }

    private void exportTango() {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getContext());
        builder.setMessage("要导出数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("包含学习信息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exportTango(true);
            }
        });
        builder.setNegativeButton("仅导出単語", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exportTango(false);
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

    private void importTango() {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getContext());
        builder.setMessage("要导入数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("文件导入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(ImportFileActivity.class);
            }
        });
        builder.setNegativeButton("文本导入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(ImportTangoActivity.class);
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

    private void deleteTango() {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getContext());
        builder.setMessage("确定要删除列出的数据吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<Long> ids = new ArrayList<>();
                List<Tango> tangoList = TangoManager.getInstance().getTangoList();
                for (Tango tango : tangoList) {
                    ids.add(tango.id);
                }
                if (TangoEntityManager.getInstance().deleteByIds(ids)) {
                    showToast("删除成功");
                    EventBus.getDefault().post(new TangoListChangeEvent());
                }
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

    private void exportTango(boolean personalFlag) {
        List<Tango> list = TangoManager.getInstance().getTangoList();
        String dir = Environment.getExternalStorageDirectory() + Constants.FILE_DIR;
        String filename = "/export.csv";
        Collection<String> items = new ArrayList<>();
        for (Tango tango : list) {
            String strings[] = new String[]{
                    tango.writing, //0
                    tango.pronunciation, //1
                    tango.meaning, //2
                    String.valueOf(tango.tone), //3
                    tango.partOfSpeech, //4
                    tango.image, //5
                    tango.voice, // 6
                    String.valueOf(tango.score), //7
                    String.valueOf(tango.frequency), //8
                    String.valueOf(tango.addTime.getTime()), //9
                    String.valueOf(tango.lastTime.getTime()), //10
                    tango.flags, //11
                    String.valueOf(tango.delFlag), //12
                    tango.type //13
            };
            if (!personalFlag) {
                strings[7] = "0"; //Score
                strings[8] = "0"; //Frequency
                strings[9] = "0"; //AddTime
                strings[10] = "0"; //LastTime
                strings[11] = ""; //Flags
                strings[12] = "0"; //DelFlag
                strings[13] = ""; //Type
            }
            String item = StrUtil.join(strings, ",");
            items.add(item);
        }
        if (CSVUtil.toCSV(items, dir + filename, "UTF-8")) {
            showToast("成功导出至:" + dir + filename);
        } else {
            showToast("导出失败");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        //TangoManager.getInstance().updateTangoList(); //在SplashActivity中调用
        tangoAdapter = new TangoAdapter(getContext(), TangoManager.getInstance().getTangoList());
        tangoList.setAdapter(tangoAdapter);
        int count = TangoEntityManager.getInstance().getCount();
        countView.setText("" + count + "/" + count);

        tangoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Tango tango = (Tango) tangoAdapter.getItem(i);
                if (itemDoubleFlag) { //双按
                    itemDoubleFlag = false;
                    String writing = tango.writing;
                    if (!writing.equals("")) {
                        SpeakTangoManager.getInstance().speak(writing);
                    }
                } else {
                    itemDoubleFlag = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (itemDoubleFlag) { //超时未按下第二次
                                //执行单按逻辑
                                showVerbDialog(tango);
                            }
                            itemDoubleFlag = false;
                        }
                    }, ViewConfiguration.getDoubleTapTimeout());
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
                        UpdateTangoDialog dialog = new UpdateTangoDialog();
                        dialog.initDialog(getContext(), tango);
                        dialog.show(getActivity().getFragmentManager(), "UPDATE_TANGO");
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
        TangoManager.getInstance().updateTangoList();
        List<Tango> tangoList = TangoManager.getInstance().getTangoList();
        tangoAdapter.updateList(tangoList);
        countView.setText("" + tangoList.size() + "/" + TangoEntityManager.getInstance().getCount());
    }

    private void showVerbDialog(Tango tango) {
        String part = tango.partOfSpeech;
        if (!part.equals("")) {
            if (part.contains(TangoConstants.VERB_FLAG)) {
                String verb = tango.writing;

                int type = 0;
                switch (tango.partOfSpeech) {
                    case TangoConstants.VERB1_FLAG:
                        type = 1;
                        break;
                    case TangoConstants.VERB2_FLAG:
                        type = 2;
                        break;
                    case TangoConstants.VERB3_FLAG:
                        type = 3;
                        break;
                }

                VerbDialog dialog = new VerbDialog();
                dialog.initDialog(getContext(), verb, type);
                dialog.show(getActivity().getFragmentManager(), "VERB");
            }
        }
    }

    @Subscribe
    public void onEvent(TangoListChangeEvent event) {
        updateTangoList();
    }

    @Subscribe
    public void onEvent(OperateTangoEvent event) {
    }

    @Subscribe
    public void onEvent(ChooseTangoEvent event) {
        int i = 0;
        for (Tango t : TangoManager.getInstance().getTangoList()) {
            if (t.id == event.tango.id) {
                break;
            }
            i++;
        }
        if (i < TangoManager.getInstance().getTangoList().size()) {
            tangoList.setSelection(i);
        }
    }

    @Subscribe
    public void onEvent(JapaneseFontChangeEvent event) {
        tangoAdapter.notifyDataSetChanged();
    }
}
