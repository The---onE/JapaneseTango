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
import com.xmx.tango.module.imp.ImportFileActivity;
import com.xmx.tango.R;
import com.xmx.tango.module.crud.AddTangoActivity;
import com.xmx.tango.module.crud.ChooseTangoEvent;
import com.xmx.tango.module.font.JapaneseFontChangeEvent;
import com.xmx.tango.module.imp.ImportNetActivity;
import com.xmx.tango.module.operate.OperateTangoEvent;
import com.xmx.tango.module.crud.SearchTangoDialog;
import com.xmx.tango.module.speaker.SpeakTangoManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoAdapter;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.module.crud.TangoListChangeEvent;
import com.xmx.tango.module.tango.TangoManager;
import com.xmx.tango.module.crud.UpdateTangoDialog;
import com.xmx.tango.module.verb.VerbDialog;
import com.xmx.tango.base.fragment.xUtilsFragment;
import com.xmx.tango.module.imp.CsvUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
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

    @Event(value = R.id.btnSearch)
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
        builder.setNegativeButton("网络导入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(ImportNetActivity.class);
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
                List<Tango> tangoList = TangoManager.INSTANCE.getTangoList();
                for (Tango tango : tangoList) {
                    ids.add(tango.getId());
                }
                if (TangoEntityManager.INSTANCE.deleteByIds(ids)) {
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
        List<Tango> list = TangoManager.INSTANCE.getTangoList();
        String dir = Environment.getExternalStorageDirectory() + Constants.FILE_DIR;
        String filename = "/export.csv";
        if (CsvUtil.INSTANCE.exportTango(dir + filename, list, personalFlag)) {
            showToast("成功导出至:" + dir + filename);
        } else {
            showToast("导出失败");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        //TangoManager.getInstance().updateTangoList(); //在SplashActivity中调用
        tangoAdapter = new TangoAdapter(getContext(), TangoManager.INSTANCE.getTangoList());
        tangoList.setAdapter(tangoAdapter);
        int count = TangoEntityManager.INSTANCE.getCount();
        countView.setText("" + count + "/" + count);

        tangoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Tango tango = (Tango) tangoAdapter.getItem(i);
                if (itemDoubleFlag) { //双按
                    itemDoubleFlag = false;
                    String writing = tango.getWriting();
                    if (!writing.equals("")) {
                        SpeakTangoManager.INSTANCE.speak(getContext(), writing);
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
                                TangoEntityManager.INSTANCE.deleteById(tango.getId());
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
        TangoManager.INSTANCE.updateTangoList();
        List<Tango> tangoList = TangoManager.INSTANCE.getTangoList();
        tangoAdapter.updateList(tangoList);
        countView.setText("" + tangoList.size() + "/" + TangoEntityManager.INSTANCE.getCount());
    }

    private void showVerbDialog(Tango tango) {
        String part = tango.getPartOfSpeech();
        if (!part.equals("")) {
            if (part.contains(TangoConstants.INSTANCE.getVERB_FLAG())) {
                String verb = tango.getWriting();

                int type = 0;
                String p = tango.getPartOfSpeech();
                if (TangoConstants.INSTANCE.getVERB1_FLAG().equals(p)) {
                    type = 1;
                } else if (TangoConstants.INSTANCE.getVERB2_FLAG().equals(p)) {
                    type = 2;
                } else if (TangoConstants.INSTANCE.getVERB3_FLAG().equals(p)) {
                    type = 3;
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
        for (Tango t : TangoManager.INSTANCE.getTangoList()) {
            if (t.getId() == event.getTango().getId()) {
                break;
            }
            i++;
        }
        if (i < TangoManager.INSTANCE.getTangoList().size()) {
            tangoList.setSelection(i);
        }
    }

    @Subscribe
    public void onEvent(JapaneseFontChangeEvent event) {
        tangoAdapter.notifyDataSetChanged();
    }
}
