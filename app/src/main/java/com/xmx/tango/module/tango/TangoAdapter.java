package com.xmx.tango.module.tango;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmx.tango.core.Constants;
import com.xmx.tango.R;
import com.xmx.tango.common.data.BaseEntityAdapter;

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
        TextView tone;
        TextView meaning;
        TextView part;
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
            holder.tone = (TextView) convertView.findViewById(R.id.item_tone);
            holder.meaning = (TextView) convertView.findViewById(R.id.item_meaning);
            holder.part = (TextView) convertView.findViewById(R.id.item_part);
            holder.time = (TextView) convertView.findViewById(R.id.item_time);

            AssetManager mgr = mContext.getAssets();
            Typeface tf = Typeface.createFromAsset(mgr, Constants.JAPANESE_FONT);
            holder.pronunciation.setTypeface(tf);
            holder.writing.setTypeface(tf);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mData.size()) {
            Tango tango = mData.get(position);
            holder.writing.setText(tango.writing);

            holder.pronunciation.setText(tango.pronunciation);
            if (tango.tone >= 0 && tango.tone < Constants.TONES.length) {
                holder.tone.setText(Constants.TONES[tango.tone]);
            } else {
                holder.tone.setText("");
            }

            if (!tango.partOfSpeech.equals("")) {
                holder.part.setText("[" + tango.partOfSpeech + "]");
            } else {
                holder.part.setText("");
            }
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