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
            int r = new Random().nextInt(size);
            return tangoList.get(r);
        } else {
            //TODO
            List<Tango> tangos = new ArrayList<>();
            Tango t1 = new Tango();
            t1.writing = "愛";
            t1.pronunciation = "アイ";
            tangos.add(t1);

            Tango t2 = new Tango();
            t2.writing = "大切";
            t2.pronunciation = "タイセツ";
            t2.meaning = "重要";
            tangos.add(t2);

            int r = new Random().nextInt(tangos.size());
            return tangos.get(r);
        }
    }
}
