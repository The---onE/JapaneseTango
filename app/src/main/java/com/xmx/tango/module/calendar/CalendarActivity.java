package com.xmx.tango.module.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by The_onE on 2017/6/25.
 */
@ContentView(R.layout.activity_calendar)
public class CalendarActivity extends BaseTempActivity {

    @ViewInject(R.id.datePicker)
    private DatePicker picker;

    Map<String, DateData> map = new HashMap<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        Date now = new Date();
        picker.setDate(now.getYear() + 1900, now.getMonth() + 1);
        picker.setTodayDisplay(true);
        picker.setMode(DPMode.NONE);

        List<String> checkIn = new ArrayList<>();
        List<String> info = new ArrayList<>();
        List<DateData> data = DateDataEntityManager.getInstance().selectAll();
        for (DateData item : data) {
            String key = item.year + "-" + item.month + "-" + item.date;
            checkIn.add(key);
            map.put(key, item);
            if (item.date != now.getDate()
                    || item.month != now.getMonth()+1
                    || item.year != now.getYear()+1900) {
                info.add(key);
            }
        }
        DPCManager.getInstance().setDecorBG(checkIn);
        DPCManager.getInstance().setDecorL(info);
        DPCManager.getInstance().setDecorT(info);
        DPCManager.getInstance().setDecorR(info);

        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorBG(canvas, rect, paint, data);
                paint.setColor(Color.GREEN);
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2F, paint);
            }

            @Override
            public void drawDecorL(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorL(canvas, rect, paint, data);
                DateData dateData = map.get(data);
                paint.setTextSize(20);
                canvas.drawText("学" + dateData.study, rect.centerX(), rect.centerY(), paint);
            }

            @Override
            public void drawDecorT(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorT(canvas, rect, paint, data);
                DateData dateData = map.get(data);
                paint.setTextSize(20);
                canvas.drawText("任" + dateData.mission, rect.centerX(), rect.centerY(), paint);
            }

            @Override
            public void drawDecorR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorR(canvas, rect, paint, data);
                DateData dateData = map.get(data);
                paint.setTextSize(20);
                canvas.drawText("复" + dateData.review, rect.centerX(), rect.centerY(), paint);
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
