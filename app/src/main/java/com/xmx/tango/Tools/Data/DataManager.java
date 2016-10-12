package com.xmx.tango.Tools.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.xmx.tango.Constants;

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

    protected int getInt(String key) {
        return mData.getInt(key, -1);
    }

    protected int getInt(String key, int def) {
        return mData.getInt(key, def);
    }

    protected void setInt(String key, int value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    protected float getFloat(String key) {
        return mData.getFloat(key, -1);
    }

    protected float getFloat(String key, float def) {
        return mData.getFloat(key, def);
    }

    protected void setFloat(String key, float value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    protected void intIncrease(String key, int delta) {
        int i = getInt(key);
        i += delta;
        setInt(key, i);
    }

    protected long getLong(String key, long def) {
        return mData.getLong(key, def);
    }

    protected void setLong(String key, long value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    protected String getString(String key) {
        return mData.getString(key, "");
    }

    protected String getString(String key, String def) {
        return mData.getString(key, def);
    }

    protected void setString(String key, String value) {
        SharedPreferences.Editor editor = mData.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public long getLastTime() {
        return getLong("last_time", 0);
    }
    public void setLastTime(long time) {
        setLong("last_time", time);
    }

    public int getTangoStudy() {
        return getInt("tango_study", 0);
    }
    public void setTangoStudy(int study) {
        setInt("tango_study", study);
    }

    public int getTangoReview() {
        return getInt("tango_review", 0);
    }
    public void setTangoReview(int review) {
        setInt("tango_review", review);
    }

    public int getTangoGoal() {
        return getInt("tango_goal", Constants.DEFAULT_GOAL);
    }
    public void setTangoGoal(int goal) {
        setInt("tango_goal", goal);
    }

    public int getReviewFrequency() {
        return getInt("review_frequency", Constants.REVIEW_FREQUENCY);
    }
    public void setReviewFrequency(int frequency) {
        setInt("review_frequency", frequency);
    }

    public float getAnswerTime() {
        return getFloat("answer_time", 2.5f);
    }
    public void setAnswerTime(float answerTime) {
        setFloat("answer_time", answerTime);
    }

    public float getMeaningTime() {
        return getFloat("meaning_time", 3.5f);
    }
    public void setMeaningTime(float meaningTime) {
        setFloat("meaning_time", meaningTime);
    }

    public String getTangoType() {
        return getString("tango_type", "");
    }
    public void setTangoType(String type) {
        setString("tango_type", type);
    }

    public String getTangoSpeaker() {
        return getString("tango_speaker", Constants.SPEAKERS[0]);
    }
    public void setTangoSpeaker(String speaker) {
        setString("tango_speaker", speaker);
    }
}
