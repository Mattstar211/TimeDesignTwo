package com.example.timedesigntwo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlertReceiver : BroadcastReceiver() {
    private lateinit var mNotificationHelper: NotificationHelper
    override fun onReceive(context: Context?, intent: Intent) {
        var descriptionName = intent.getStringExtra("descriptionName")
        mNotificationHelper = NotificationHelper(context)
        val descriptionString = "У вас запланировано событие: " + descriptionName.toString()
        val description = intent.getStringExtra("description")
        val nb: NotificationCompat.Builder =
            mNotificationHelper.getChannel1Notification(descriptionString, description.toString())
        mNotificationHelper.getManager().notify(System.currentTimeMillis().toInt(), nb.build())
    }
}