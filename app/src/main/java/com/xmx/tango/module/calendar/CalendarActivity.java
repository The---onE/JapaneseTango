package com.xmx.tango.module.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;

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
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
