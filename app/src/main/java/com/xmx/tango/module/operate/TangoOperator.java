package com.xmx.tango.module.operate;

import com.xmx.tango.core.Constants;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.calendar.DateData;
import com.xmx.tango.module.calendar.DateDataEntityManager;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoConstants;
import com.xmx.tango.module.tango.TangoEntityManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by The_onE on 2016/10/7.
 */
public class TangoOperator {
    public int study;
    public int review;
    int todayConsecutive = 0;

    private Tango prevTango;
    private int prevStudy;
    private int prevReview;

    private static TangoOperator instance;

    public synchronized static TangoOperator getInstance() {
        if (null == instance) {
            instance = new TangoOperator();
        }
        return instance;
    }

    private TangoOperator() {
        study = DataManager.getInstance().getTangoStudy();
        review = DataManager.getInstance().getTangoReview();

        Date last = new Date(DataManager.getInstance().getResetLastTime());
        Date now = new Date();
        if (!Constants.isSameDate(now, last)) {
            if (last.getTime() > 0) {
                // 更新上次签到日数据
                DateData dateData = DateDataEntityManager.INSTANCE
                        .selectLatest("addTime", false,
                                "Year=" + (last.getYear() + 1900),
                                "Month=" + (last.getMonth() + 1),
                                "Date=" + last.getDate());
                if (dateData != null) {
                    DateDataEntityManager.INSTANCE.updateData(dateData.getId(),
                            "Study=" + study,
                            "Review=" + review);
                } else {
                    dateData = new DateData();
                    dateData.setYear(last.getYear() + 1900);
                    dateData.setMonth(last.getMonth() + 1);
                    dateData.setDate(last.getDate());
                    dateData.setCheckIn(1);
                    dateData.setStudy(study);
                    dateData.setReview(review);
                    dateData.setAddTime(now);
                    DateDataEntityManager.INSTANCE.insertData(dateData);
                }
            }
            // 今天打卡签到
            DateData todayData = DateDataEntityManager.INSTANCE
                    .selectLatest("addTime", false,
                            "Year=" + (now.getYear() + 1900),
                            "Month=" + (now.getMonth() + 1),
                            "Date=" + now.getDate());
            if (todayData == null) {
                todayData = new DateData();
                todayData.setYear(now.getYear() + 1900);
                todayData.setMonth(now.getMonth() + 1);
                todayData.setDate(now.getDate());
                todayData.setCheckIn(1);
                todayData.setAddTime(now);
                DateDataEntityManager.INSTANCE.insertData(todayData);
            }
            study = 0;
            DataManager.getInstance().setTangoStudy(0);
            review = 0;
            DataManager.getInstance().setTangoReview(0);

            DataManager.getInstance().setReviewFrequency(TangoConstants.REVIEW_FREQUENCY);

            DataManager.getInstance().setResetLastTime(now.getTime());
        }
    }

    public void remember(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            int goal = DataManager.getInstance().getTangoGoal();
            if (!Constants.isSameDate(now, last)) {
                todayConsecutive = 0;
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setTangoReview(review);
                    //DataManager.getInstance().setLastTime(now.getTime());
                    if (frequency > 0) {
                        frequency--;
                    }
                } else { //学习
                    study++;
                    frequency = TangoConstants.REVIEW_FREQUENCY;
                    DataManager.getInstance().setTangoStudy(study);
                }
            } else if (study >= goal) {
                todayConsecutive++;
                if (todayConsecutive > TangoConstants.TODAY_CONSECUTIVE_REVIEW_MAX) {
                    todayConsecutive = 0;
                    int frequencyMax = DataManager.getInstance().getReviewFrequency();
                    frequencyMax--;
                    DataManager.getInstance().setReviewFrequency(frequencyMax);
                }
            }

            int score = TangoConstants.REMEMBER_SCORE - (study + review) / TangoConstants.TIRED_COEFFICIENT;
            score = Math.max(score, TangoConstants.REMEMBER_MIN_SCORE);
            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + score),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void forget(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!Constants.isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    frequency++;
                    if (frequency > TangoConstants.REVIEW_FREQUENCY) {
                        frequency = TangoConstants.REVIEW_FREQUENCY;
                    }
                }
            }

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.FORGET_SCORE),
                    "Frequency=" + frequency);
            //"LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void rememberForever(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!Constants.isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setTangoReview(review);
                    //DataManager.getInstance().setLastTime(now.getTime());
                } else { //学习
                    study++;
                    DataManager.getInstance().setTangoStudy(study);
                    //DataManager.getInstance().setLastTime(now.getTime());
                }
            }
            frequency = -1;

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_FOREVER_SCORE),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void rightWithoutHint(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!Constants.isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setTangoReview(review);
                }
                if (frequency > 0) {
                    frequency -= 2;
                    if (frequency < 0) {
                        frequency = 0;
                    }
                }
            } else {
                todayConsecutive++;
                if (todayConsecutive > TangoConstants.TODAY_CONSECUTIVE_REVIEW_MAX) {
                    todayConsecutive = 0;
                    int frequencyMax = DataManager.getInstance().getReviewFrequency();
                    frequencyMax--;
                    DataManager.getInstance().setReviewFrequency(frequencyMax);
                }
            }

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_SCORE * 2),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void rightWithHint(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!Constants.isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    review++;
                    DataManager.getInstance().setTangoReview(review);
                }
                if (frequency > 0) {
                    frequency--;
                }
            }

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.REMEMBER_SCORE),
                    "Frequency=" + frequency,
                    "LastTime=" + new Date().getTime());
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void wrong(Tango tango) {
        if (tango != null && tango.id > 0) {
            prevTango = tango;
            prevStudy = study;
            prevReview = review;

            Date last = tango.lastTime;
            Date now = new Date();
            int frequency = tango.frequency;
            if (!Constants.isSameDate(now, last)) {
                if (last.getTime() > 0) { //复习
                    frequency++;
                    if (frequency > TangoConstants.REVIEW_FREQUENCY) {
                        frequency = TangoConstants.REVIEW_FREQUENCY;
                    }
                }
            }

            TangoEntityManager.getInstance().updateData(tango.id,
                    "Score=" + (tango.score + TangoConstants.FORGET_SCORE / 2),
                    "Frequency=" + frequency);
            EventBus.getDefault().post(new OperateTangoEvent());
        }
    }

    public void cancelOperate() {
        if (prevTango != null && prevTango.id > 0) {
            TangoEntityManager.getInstance().updateData(prevTango.id,
                    "Score=" + prevTango.score,
                    "Frequency=" + prevTango.frequency,
                    "LastTime=" + prevTango.lastTime.getTime());

            study = prevStudy;
            review = prevReview;
        }
    }
}
