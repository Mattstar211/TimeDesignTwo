package com.example.timedesigntwo

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity

class AppWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET) {
            val description = intent.getStringExtra(EXTRA_DESCRIPTION)
            val time = intent.getStringExtra(EXTRA_TIME)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (appWidgetIds != null) {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidgetData(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        description.toString(),
                        time.toString()
                    )
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            // Регистрация приемника широковещательных сообщений для обновления виджета
            val intent = Intent(context, AppWidgetProvider::class.java)
            intent.action = ACTION_UPDATE_WIDGET
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            // Обновление виджета каждую минуту
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis(),
                1000,
                pendingIntent
            )

            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Создаем RemoteViews для виджета
        val views = RemoteViews(context.packageName, R.layout.app_widget)

        // Обновляем текст в TextView виджета
        views.setTextViewText(R.id.widgetTextView, "Updated Text")

        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.timedesigntwo.ACTION_UPDATE_WIDGET"
        const val EXTRA_DESCRIPTION = "com.example.timedesigntwo.EXTRA_DESCRIPTION"
        const val EXTRA_TIME = "com.example.timedesigntwo.EXTRA_TIME"
    }

    private fun updateAppWidgetData(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        description: String,
        time: String
    ) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        views.setTextViewText(R.id.widgetTextView, description)
        views.setTextViewText(R.id.timeTextView, time)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}
