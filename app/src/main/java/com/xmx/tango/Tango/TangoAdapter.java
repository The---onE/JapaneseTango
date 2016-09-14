package com.xmx.tango.Tango;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.Tools.Data.BaseEntityAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by The_onE on 2016/3/27.
 */
public class TangoAdapter extends BaseEntityAdapter<Tango> {

    public TangoAdapter(Context context, List<Tango> data) {
        super(context, data);
    }

    static class ViewHolder {
        TextView writing;
        TextView pronunciation;
        TextView meaning;
        TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tango, null);
            holder = new ViewHolder();
            holder.writing = (TextView) convertView.findViewById(R.id.item_writing);
            holder.pronunciation = (TextView) convertView.findViewById(R.id.item_pronunciation);
            holder.meaning = (TextView) convertView.findViewById(R.id.item_meaning);
            holder.time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mData.size()) {
            Tango tango = mData.get(position);
            holder.writing.setText(tango.writing);
            holder.pronunciation.setText(tango.pronunciation);
            holder.meaning.setText(tango.meaning);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeString = df.format(tango.addTime);
            holder.time.setText(timeString);
        } else {
            holder.writing.setText("加载失败");
        }

        return convertView;
    }
}