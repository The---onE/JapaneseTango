package com.xmx.tango.common.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat

import com.xmx.tango.R

/**
 * Created by wli on 15/8/26.
 * 通知栏通知管理工具
 */
object NotificationUtils {

    /**
     * 显示通知栏通知
     * @param content 当前上下文
     * @param id 通知ID
     * @param intent 点击后进行的Intent
     * @param title 通知标题
     * @param context 通知内容
     * @param autoCancelFlag 是否自动取消
     * @param onGoingFlag 是否正在进行
     * @param sIcon 通知图标
     * @param sound 通知提示音
     */
    fun showNotification(context: Context, id: Int, intent: Intent,
                         title: String, content: String,
                         autoCancelFlag: Boolean = true,
                         onGoingFlag: Boolean = false,
                         sIcon: Int = R.mipmap.ic_launcher,
                         sound: String? = null) {
        // 生成用于通知的PendingIntent
        val contentIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        // 生成通知
        val mBuilder = NotificationCompat.Builder(context, content)
                .setSmallIcon(sIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancelFlag)
                .setOngoing(onGoingFlag)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_ALL)
        val notification = mBuilder.build()
        if (sound != null && sound.isNotBlank()) {
            notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound)
        }
        // 发送通知
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }

    /**
     * 移除通知
     * @param context 当前上下文
     * @param id 通知ID
     */
    fun removeNotification(context: Context, id: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }
}
