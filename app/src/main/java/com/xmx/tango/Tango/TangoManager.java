package com.xmx.tango.Tango;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by The_onE on 2016/9/13.
 */
public class TangoManager {
    private static final int SELECT_TANGO_LIMIT = 8;

    private static TangoManager instance;

    public String writing;
    public String pronunciation;
    public String meaning;
    public String partOfSpeech;
    public String type;

    public String order = "ID";
    public boolean ascFlag = true;

    public synchronized static TangoManager getInstance() {
        if (null == instance) {
            instance = new TangoManager();
        }
        return instance;
    }

    //long tangoVersion = 0;
    long version = System.currentTimeMillis();
    List<Tango> tangoList = new ArrayList<>();
    Random random = new Random();

    int index = 0;
    List<Tango> tempTangos = new ArrayList<>();

    private TangoManager() {
        Tango t1 = new Tango();
        t1.id = -1;
        t1.writing = "愛";
        t1.pronunciation = "あい";
        t1.tone = 1;
        tempTangos.add(t1);

        Tango t2 = new Tango();
        t2.id = -2;
        t2.writing = "大切";
        t2.pronunciation = "たいせつ";
        t2.tone = 0;
        t2.meaning = "重要,珍贵";
        t2.partOfSpeech = "形容动词";
        tempTangos.add(t2);

        Tango t3 = new Tango();
        t3.id = -3;
        t3.writing = "ありがとうございます";
        t3.pronunciation = "ありがとうございます";
        t3.meaning = "谢谢";
        t3.partOfSpeech = "惯用语";
        tempTangos.add(t3);

        Tango t4 = new Tango();
        t4.id = -4;
        t4.writing = "よろしくお願い申し上げます";
        t4.pronunciation = "よろしくおねがいもうしあげます";
        t4.meaning = "请多关照";
        t4.partOfSpeech = "惯用语";
        tempTangos.add(t4);
    }

    public List<Tango> getData() {
        return tangoList;
    }

    public long updateData() {
        TangoEntityManager manager = TangoEntityManager.getInstance();
        //tangoVersion = manager.getVersion();

        tangoList.clear();
        //tangoList = manager.selectAll("addTime", false);
        List<String> con = new ArrayList<>();
        if (writing != null && !writing.equals("")) {
            con.add("Writing like '%" + writing + "%'");
        }
        if (pronunciation != null && !pronunciation.equals("")) {
            con.add("Pronunciation like '%" + pronunciation + "%'");
        }
        if (meaning != null && !meaning.equals("")) {
            con.add("Meaning like '%" + meaning + "%'");
        }
        if (partOfSpeech != null && !partOfSpeech.equals("")) {
            con.add("PartOfSpeech like '%" + partOfSpeech + "%'");
        }
        if (type != null && !type.equals("")) {
            con.add("Type like '%" + type + "%'");
        }

        String array[] = new String[con.size()];
        array = con.toArray(array);
        tangoList = manager.selectByCondition(order, ascFlag, array);

        version++;
        return version;
    }

    public List<Tango> getTangoList(boolean reviewFlag, int maxFrequency) {
        List<Tango> tangos = TangoEntityManager.getInstance()
                .selectTangoScoreAsc(SELECT_TANGO_LIMIT, reviewFlag, maxFrequency);
        return tangos;
    }

    public Tango randomTango(boolean reviewFlag, int maxFrequency) {
        List<Tango> tangos = getTangoList(reviewFlag, maxFrequency);
        int size = tangos.size();
        if (size > 0) {
            index = random.nextInt(size);
            return tangos.get(index);
        } else {
            //TODO
            index = random.nextInt(tempTangos.size());
            return tempTangos.get(index);
        }
    }

    public Tango nextTango(boolean reviewFlag, int maxFrequency) {
        List<Tango> tangos = getTangoList(reviewFlag, maxFrequency);
        int size = tangos.size();
        if (size > 0) {
            index = (++index) % size;
            return tangos.get(index);
        } else {
            //TODO
            index = (++index) % tempTangos.size();
            return tempTangos.get(index);
        }
    }
}
