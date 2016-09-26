package com.xmx.tango.Tools.Data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by The_onE on 2016/2/21.
 */
public class DataManager {
    private static DataManager instance;

    Context mContext;
    SharedPreferences mData;

    public synchronized static DataManager getInstance() {
        if (null == instance) {
            instance = new DataManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
        mData = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
    }

    public long getVersion() {
        return mData.getLong("version", -1);
    }

    public void setVersion(long value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putLong("version", value);
        editor.apply();
    }

    public int getInt(String key) {
        return mData.getInt(key, -1);
    }

    public int getInt(String key, int def) {
        return mData.getInt(key, def);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return mData.getFloat(key, -1);
    }

    public float getFloat(String key, float def) {
        return mData.getFloat(key, def);
    }

    public void setFloat(String key, float value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void intIncrease(String key, int delta) {
        int i = getInt(key);
        i += delta;
        setInt(key, i);
    }

    public long getLong(String key, long def) {
        return mData.getLong(key, def);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return mData.getString(key, "");
    }

    public String getString(String key, String def) {
        return mData.getString(key, def);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
