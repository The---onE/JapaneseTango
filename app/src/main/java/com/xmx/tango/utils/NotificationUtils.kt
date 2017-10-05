package com.xmx.tango.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import com.xmx.tango.R

import java.util.LinkedList

/**
 * Created by wli on 15/8/26.
 * 消息通知处理类
 */
object NotificationUtils {

    /**
     * 显示消息通知
     * @param[context] 当前上下文
     * @param[intent] 点击通知后的Intent
     * @param[id] 通知标识
     * @param[title] 通知标题
     * @param[content] 通知内容
     * @param[autoCancelFlag] 是否自动取消
     * @param[onGoingFlag] 是否为持续通知
     * @param[sIcon] 显示图标ID
     * @param[sound] 声音资源
     */
    fun showNotification(context: Context,
                         id: Int,
                         intent: Intent,
                         title: String,
                         content: String,
                         autoCancelFlag: Boolean = true,
                         onGoingFlag: Boolean = false,
                         sIcon: Int = R.mipmap.ic_launcher,
                         sound: String? = null) {
        // 设置点击通知后执行的Intent
        val contentIntent = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        // 设置消息通知
        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(sIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancelFlag)
                .setOngoing(onGoingFlag)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 生成消息通知
        val notification = mBuilder.build()
        if (sound.isNullOrEmpty()) {
            notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound)
        }
        // 发送消息通知
        manager.notify(id, notification)
    }

    /**
     * 根据通知标识移除通知
     * @param[context] 当前上下文
     * @param[id] 通知标识
     */
    fun removeNotification(context: Context, id: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }
}
