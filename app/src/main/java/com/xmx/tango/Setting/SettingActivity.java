package com.xmx.tango.Setting;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xmx.tango.R;
import com.xmx.tango.Tools.ActivityBase.BaseTempActivity;
import com.xmx.tango.Tools.Data.DataManager;

/**
 * Created by The_onE on 2016/9/17.
 */
public class SettingActivity extends BaseTempActivity {

    EditText typeView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);

        typeView = getViewById(R.id.edit_type);
        typeView.setText(DataManager.getInstance().getString("tango_type"));
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_change_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = typeView.getText().toString().trim();
                DataManager.getInstance().setString("tango_type", type);
                showToast("更改成功");
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
