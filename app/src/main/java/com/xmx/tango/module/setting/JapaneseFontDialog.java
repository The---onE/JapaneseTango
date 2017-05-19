package com.xmx.tango.module.setting;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.base.dialog.BaseDialog;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.tango.JapaneseFontChangeEvent;
import com.xmx.tango.module.tango.TangoConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

/**
 * Created by The_onE on 2017/5/19.
 */

public class JapaneseFontDialog extends BaseDialog {
    ListView fontList;
    String[] keyArray;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_japanese_font, container);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        fontList = (ListView) view.findViewById(R.id.list_font);
        Set<String> keySet = TangoConstants.JAPANESE_FONT_MAP.keySet();
        keyArray = keySet.toArray(new String[keySet.size()]);
        ListAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return keyArray.length;
            }

            @Override
            public Object getItem(int i) {
                return keyArray[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_japanese_font, null);
                TextView fontView = (TextView) view.findViewById(R.id.item_font);
                fontView.setText(keyArray[i]);
                AssetManager mgr = mContext.getAssets();
                String font = TangoConstants.
                        JAPANESE_FONT_MAP.get(keyArray[i]);
                Typeface tf = Typeface.DEFAULT;
                if (font != null) {
                    tf = Typeface.createFromAsset(mgr, font);
                }
                fontView.setTypeface(tf);
                return view;
            }
        };
        fontList.setAdapter(adapter);
    }

    @Override
    protected void setListener(View view) {
        fontList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = keyArray[i];
                DataManager.getInstance().setJapaneseFontTitle(keyArray[i]);
                EventBus.getDefault().post(new JapaneseFontChangeEvent());
                dismiss();
            }
        });
    }

    @Override
    protected void processLogic(View view, Bundle savedInstanceState) {

    }
}
