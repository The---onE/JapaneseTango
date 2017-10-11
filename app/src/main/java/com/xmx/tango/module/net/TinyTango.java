package com.xmx.tango.module.net;

/**
 * Created by The_onE on 2017/10/11.
 * 便于网络传输的单语实体
 */
public class TinyTango {
    private String writing = "";
    private String pronunciation = "";
    private String meaning = "";
    private int tone = -1;
    private String partOfSpeech = "";

    public TinyTango() {
    }

    public TinyTango(String writing, String pronunciation, String meaning, int tone, String partOfSpeech) {
        this.writing = writing;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
        this.tone = tone;
        this.partOfSpeech = partOfSpeech;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getTone() {
        return tone;
    }

    public void setTone(int tone) {
        this.tone = tone;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}
