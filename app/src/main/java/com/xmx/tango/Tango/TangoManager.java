package com.xmx.tango.Tango;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by The_onE on 2016/9/13.
 */
public class TangoManager {
    static final int SELECT_TANGO_LIMIT = 8;

    private static TangoManager instance;

    public String writing;
    public String pronunciation;
    public String meaning;
    public String partOfSpeech;
    public String type;

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
        t1.pronunciation = "アイ";
        tempTangos.add(t1);

        Tango t2 = new Tango();
        t2.id = -2;
        t2.writing = "大切";
        t2.pronunciation = "タイセツ";
        t2.meaning = "重要";
        tempTangos.add(t2);
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
        tangoList = manager.selectByCondition("addTime", false, array);

        version++;
        return version;
    }

    public Tango randomTango(boolean reviewFlag) {
        List<Tango> tangos = TangoEntityManager.getInstance()
                .selectTangoScoreAsc(SELECT_TANGO_LIMIT, reviewFlag);
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

    public Tango nextTango(boolean reviewFlag) {
        List<Tango> tangos = TangoEntityManager.getInstance()
                .selectTangoScoreAsc(SELECT_TANGO_LIMIT, reviewFlag);
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
