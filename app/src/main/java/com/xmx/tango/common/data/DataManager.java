package com.xmx.tango.common.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.xmx.tango.module.tango.TangoConstants;

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

    public long getForgetLastTime() {
        return getLong("last_forget_time", 0);
    }
    public void setForgetLastTime(long time) {
        setLong("last_forget_time", time);
    }

    public long getResetLastTime() {
        return getLong("last_reset_time", 0);
    }
    public void setResetLastTime(long time) {
        setLong("last_reset_time", time);
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

    public int getTodayMission() {
        return getInt("today_mission", 0);
    }
    public void setTodayMission(int mission) {
        setInt("today_mission", mission);
    }

    public int getTangoGoal() {
        return getInt("tango_goal", TangoConstants.DEFAULT_GOAL);
    }
    public void setTangoGoal(int goal) {
        setInt("tango_goal", goal);
    }

    public int getReviewFrequency() {
        return getInt("review_frequency", TangoConstants.REVIEW_FREQUENCY);
    }
    public void setReviewFrequency(int frequency) {
        setInt("review_frequency", frequency);
    }

    public float getPronunciationTime() {
        return getFloat("pronunciation_time", 2.5f);
    }
    public void setPronunciationTime(float writingTime) {
        setFloat("pronunciation_time", writingTime);
    }

    public float getWritingTime() {
        return getFloat("writing_time", 3.0f);
    }
    public void setWritingTime(float writingTime) {
        setFloat("writing_time", writingTime);
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

    public String getPartOfSpeech() {
        return getString("tango_part_of_speech", "");
    }
    public void setPartOfSpeech(String part) {
        setString("tango_part_of_speech", part);
    }

    public String getTangoSpeaker() {
        return getString("tango_speaker", TangoConstants.SPEAKERS[0]);
    }
    public void setTangoSpeaker(String speaker) {
        setString("tango_speaker", speaker);
    }

    public int getMissionCount() {
        return getInt("mission_count", TangoConstants.MISSION_COUNT);
    }
    public void setMissionCount(int count) {
        setInt("mission_count", count);
    }
}
