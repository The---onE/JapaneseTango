package com.xmx.tango.Tango;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by The_onE on 2016/9/13.
 */
public class TangoManager {
    private static TangoManager instance;

    public synchronized static TangoManager getInstance() {
        if (null == instance) {
            instance = new TangoManager();
        }
        return instance;
    }

    long tangoVersion = 0;
    long version = System.currentTimeMillis();
    List<Tango> tangoList = new ArrayList<>();
    Random random = new Random();

    int index = 0;
    List<Tango> tempTangos = new ArrayList<>();
    private TangoManager() {
        Tango t1 = new Tango();
        t1.id = 0;
        t1.writing = "愛";
        t1.pronunciation = "アイ";
        tempTangos.add(t1);

        Tango t2 = new Tango();
        t2.id = 1;
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
        if (manager.getVersion() != tangoVersion) {
            tangoVersion = manager.getVersion();

            tangoList.clear();
            tangoList = manager.selectAll("LastTime", false);

            version++;
        }
        return version;
    }

    public Tango randomTango() {
        updateData();
        int size = tangoList.size();
        if (size > 0) {
            index = random.nextInt(size);
            return tangoList.get(index);
        } else {
            //TODO
            index = random.nextInt(tempTangos.size());
            return tempTangos.get(index);
        }
    }

    public Tango nextTango() {
        updateData();
        int size = tangoList.size();
        if (size > 0) {
            index = (++index) % size;
            return tangoList.get(index);
        } else {
            //TODO
            index = (++index) % tempTangos.size();
            return tempTangos.get(index);
        }
    }
}
