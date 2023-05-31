package com.example.timedesigntwo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private val channel1ID = "channel1ID"
    private val channel1Name = "Channel 1"
    private val channel2ID = "channel1ID"
    private val channel2Name = "Channel 2"
    private lateinit var mManager: NotificationManager;

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels() {
        val channel1 =
            NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT)
        channel1.enableLights(true)
        channel1.enableVibration(true)
        channel1.lightColor = (com.google.android.material.R.color.design_default_color_primary)
        channel1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getManager().createNotificationChannel(channel1)


        val channel2 =
            NotificationChannel(channel2ID, channel2Name, NotificationManager.IMPORTANCE_DEFAULT)
        channel2.enableLights(true)
        channel2.enableVibration(true)
        channel2.lightColor = (com.google.android.material.R.color.design_default_color_primary)
        channel2.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getManager().createNotificationChannel(channel2)
    }

    fun getManager(): NotificationManager {
        if (!::mManager.isInitialized) {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager
    }

    fun getChannel1Notification(
        title: String = "",
        message: String = ""
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channel1ID).setContentTitle(title)
            .setContentText(message).setSmallIcon(R.drawable.ic_notification)
    }
}