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
import java.util.List;

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

    @Override
    protected void initView(Bundle savedInstanceState) {
        Date now = new Date();
        picker.setDate(now.getYear() + 1900, now.getMonth() + 1);
        picker.setTodayDisplay(true);
        picker.setMode(DPMode.NONE);

        List<String> tmp = new ArrayList<>();
        List<DateData> data = DateDataEntityManager.getInstance().selectAll();
        for (DateData item : data) {
            tmp.add(item.year + "-" + item.month + "-" + item.date);
        }
        DPCManager.getInstance().setDecorBG(tmp);

        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.GREEN);
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2F, paint);
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
