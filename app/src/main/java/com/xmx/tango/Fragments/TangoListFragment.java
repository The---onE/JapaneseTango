package com.xmx.tango.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xmx.tango.Constants;
import com.xmx.tango.Import.ImportFileActivity;
import com.xmx.tango.Import.ImportTangoActivity;
import com.xmx.tango.R;
import com.xmx.tango.Tango.AddTangoActivity;
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
import com.xmx.tango.Tools.Utils.StrUtil;

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

    @Event(value = R.id.btn_search)
    private void onSearchClick(View view) {
        SearchTangoDialog dialog = new SearchTangoDialog(getContext());
        dialog.show();
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
                List<Tango> tangoList = TangoManager.getInstance().getData();
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
        List<Tango> list = TangoManager.getInstance().getData();
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
        //TangoManager.getInstance().updateData(); //在SplashActivity中调用
        tangoAdapter = new TangoAdapter(getContext(), TangoManager.getInstance().getData());
        tangoList.setAdapter(tangoAdapter);
        int count = TangoEntityManager.getInstance().getCount();
        countView.setText("" + count + "/" + count);

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
        List<Tango> tangoList = TangoManager.getInstance().getData();
        tangoAdapter.updateList(tangoList);
        countView.setText("" + tangoList.size() + "/" + TangoEntityManager.getInstance().getCount());
    }

    @Subscribe
    public void onEvent(TangoListChangeEvent event) {
        updateTangoList();
    }

    @Subscribe
    public void onEvent(OperateTangoEvent event) {
    }
}
