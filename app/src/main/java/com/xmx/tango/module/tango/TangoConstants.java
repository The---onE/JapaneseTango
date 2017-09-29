package com.xmx.tango.module.tango;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The_onE on 2017/5/16.
 */

public class TangoConstants {
    public static final String SPEAKERS[] = {"male01", "female01", "male02"};

    public static final String TONES[] = {"◎", "①", "②", "③", "④", "⑤", "⑥", "⑦"};

    public static final int DEFAULT_PRONUNCIATION_TEXT_SIZE = 36;
    public static final int DEFAULT_WRITING_TEXT_SIZE = 42;
    public static final int DEFAULT_MEANING_TEXT_SIZE = 36;

    public static final int DEFAULT_TEST_WRITING_TEXT_SIZE = 36;
    public static final int DEFAULT_TEST_MEANING_TEXT_SIZE = 42;

    public static final int REMEMBER_SCORE = 7;
    public static final int TIRED_COEFFICIENT = 35;
    public static final int REMEMBER_MIN_SCORE = 4;
    public static final int FORGET_SCORE = -3;
    public static final int REMEMBER_FOREVER_SCORE = 64;
    public static final int REVIEW_FREQUENCY = 5;
    public static final int TODAY_CONSECUTIVE_REVIEW_MAX = 10;

    public static final int MISSION_COUNT = 20;

    public static final long INTERVAL_TIME_MIN = 500;
    public static final long NEW_TANGO_DELAY = 1000;

    public static final long SHOW_ANSWER_DELAY = 2000;

    public static final int DEFAULT_GOAL = 30;

    public static final long KEYBOARD_INPUT_VIBRATE_TIME = 50;
    public static final long TEST_RIGHT_VIBRATE_TIME = 200;
    public static final long REMEMBER_VIBRATE_TIME = 100;
    public static final long REMEMBER_FOREVER_VIBRATE_TIME = 200;
    public static final long FORGET_VIBRATE_TIME = 100;

    public static int FORGOTTEN_SCORE(int source) {
        return source * 4 / 5;
    }

    public static final String VERB_FLAG = "动";
    public static final String VERB1_FLAG = "动1";
    public static final String VERB2_FLAG = "动2";
    public static final String VERB3_FLAG = "动3";

    public static final Map<String, String> JAPANESE_FONT_MAP = new LinkedHashMap<>();

    static {
        JAPANESE_FONT_MAP.put("默认", null);
        JAPANESE_FONT_MAP.put("A-OTF 毎日新聞", "A-OTF-MNewsMPro-Light.otf");
        JAPANESE_FONT_MAP.put("A-OTF くもやじ", "A-OTF-KumoyaStd-Regular.otf");
        JAPANESE_FONT_MAP.put("京円", "京円.ttf");
    }
}
