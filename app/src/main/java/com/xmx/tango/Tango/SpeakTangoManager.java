package com.xmx.tango.Tango;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.xmx.tango.Constants;
import com.xmx.tango.Tools.Data.DataManager;

import net.gimite.jatts.JapaneseTextToSpeech;

import java.util.HashMap;

/**
 * Created by The_onE on 2016/9/20.
 */
public class SpeakTangoManager {
    private static SpeakTangoManager instance;

    Context mContext;

    public synchronized static SpeakTangoManager getInstance() {
        if (null == instance) {
            instance = new SpeakTangoManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void speak(String tango) {
        JapaneseTextToSpeech tts = new JapaneseTextToSpeech(mContext, null);
        HashMap<String, String> params = new HashMap<>();
        String speaker = DataManager.getInstance().getString("tango_speaker", Constants.SPEAKERS[0]);
        params.put(JapaneseTextToSpeech.KEY_PARAM_SPEAKER, speaker);
        tts.speak(tango, TextToSpeech.QUEUE_FLUSH, params);
    }
}
