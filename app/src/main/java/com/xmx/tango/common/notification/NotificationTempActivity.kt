package com.xmx.tango.common.notification

import android.app.Activity
import android.os.Bundle

import com.xmx.tango.R

/**
 * Created by The_onE on 2017/10/16.
 * 用于点击后移除通知的临时Activity
 */
class NotificationTempActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_temp)

        // 根据ID移除通知
        val id = intent.getIntExtra("notificationId", 0)
        NotificationUtils.removeNotification(this, id)

        finish()
    }
}
