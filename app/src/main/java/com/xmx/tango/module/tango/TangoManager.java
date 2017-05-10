package com.xmx.tango.module.tango;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by The_onE on 2016/9/13.
 */
public class TangoManager {
    private static final int DEFAULT_LIMIT = 8; // 默认待选单语列表数量

    private static TangoManager instance;

    // 展示列表筛选条件
    public String writing;
    public String pronunciation;
    public String meaning;
    public String partOfSpeech;
    public String type;

    // 更新列表排序条件
    public String order = "ID";
    public boolean ascFlag = true;

    public synchronized static TangoManager getInstance() {
        if (null == instance) {
            instance = new TangoManager();
        }
        return instance;
    }

    //long tangoVersion = 0;
    private long version = System.currentTimeMillis(); // 当前版本
    private List<Tango> tangoList = new ArrayList<>(); // 用于显示的单语列表
    private List<Tango> waitingList = new ArrayList<>(); // 待选单语列表
    private Random random = new Random();

    private int index = 0; // 当前选择的单语在待选列表中的索引
    private List<Tango> tempTangos = new ArrayList<>(); // 待选列表中没有单语时显示的临时单语

    /**
     * 初始化临时列表
     */
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

    public List<Tango> getTangoList() {
        return tangoList;
    }

    /**
     * 从数据库获取用于展示的符合筛选条件的单语数据
     *
     * @return 当前版本
     */
    public long updateTangoList() {
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

    public List<Tango> getWaitingList() {
        return waitingList;
    }

    /**
     * 更新待选列表
     *
     * @param reviewFlag   是否为复习状态
     * @param maxFrequency 复习频率上限
     * @return 待选列表
     */
    public List<Tango> updateWaitingList(boolean reviewFlag, int maxFrequency) {
        return updateWaitingList(reviewFlag, maxFrequency, DEFAULT_LIMIT);
    }

    /**
     * 更新待选列表
     *
     * @param reviewFlag   是否为复习状态
     * @param maxFrequency 复习频率上限
     * @param limit        选取单语上限
     * @return 待选列表
     */
    public List<Tango> updateWaitingList(boolean reviewFlag, int maxFrequency, int limit) {
        waitingList = TangoEntityManager.getInstance()
                .selectTangoScoreAsc(limit, reviewFlag, maxFrequency);
        return waitingList;
    }

    /**
     * 在待选列表中添加单语
     *
     * @param tango 待添加单语
     */
    public void addToWaitingList(Tango tango) {
        for (Tango t : waitingList) {
            if (t.id == tango.id) {
                // 若待选列表中已存在则不添加
                return;
            }
        }
        waitingList.add(tango);
    }

    /**
     * 在待选列表中删除单语
     *
     * @param tango 待删除单语
     */
    public void removeFromWaitingList(Tango tango) {
        waitingList.remove(tango);
    }

    /**
     * 随机选取单语
     *
     * @param reviewFlag   是否为复习状态
     * @param maxFrequency 复习频率上限
     * @param prevTango    上一个单语
     * @param missionFlag  是否为任务模式
     * @return 随机选取的单语
     */
    public Tango randomTango(boolean reviewFlag, int maxFrequency, Tango prevTango,
                             boolean missionFlag) {
        if (!missionFlag) {
            // 非任务模式每次刷新列表
            updateWaitingList(reviewFlag, maxFrequency);
        }
        Tango temp;
        if (waitingList.size() > 0) {
            // 从待选列表随机选取
            int size = waitingList.size();
            index = random.nextInt(size);
            temp = waitingList.get(index);
            if (prevTango != null && temp.id == prevTango.id) {
                index = (++index) % size;
                temp = waitingList.get(index);
            }
        } else {
            //TODO 待选列表没有符合条件的单语
            index = random.nextInt(tempTangos.size());
            temp = tempTangos.get(index);
            if (prevTango != null && temp.id == prevTango.id) {
                index = (++index) % tempTangos.size();
                temp = tempTangos.get(index);
            }
        }
        return temp;
    }

//    public Tango nextTango(boolean reviewFlag, int maxFrequency) {
//        List<Tango> tangos = getTangoList(reviewFlag, maxFrequency);
//        int size = tangos.size();
//        if (size > 0) {
//            index = (++index) % size;
//            return tangos.get(index);
//        } else {
//            //TODO
//            index = (++index) % tempTangos.size();
//            return tempTangos.get(index);
//        }
//    }
}
