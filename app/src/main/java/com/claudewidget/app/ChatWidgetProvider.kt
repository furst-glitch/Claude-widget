package com.claudewidget.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Hjemmeskaerm-widget. Viser den seneste besked-udveksling direkte
 * (robust, uden scrollbar collection) og en "skriv besked"-bjaelke.
 */
class ChatWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(
                ComponentName(context, ChatWidgetProvider::class.java)
            )
            for (id in ids) {
                updateWidget(context, mgr, id)
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_chat)

        // Hent de seneste beskeder og vis dem direkte
        val messages = try {
            ChatStorage.getMessages(context)
        } catch (e: Exception) {
            emptyList()
        }

        val lastUser = messages.lastOrNull { it.role == "user" }?.content
        val lastReply = messages.lastOrNull { it.role == "assistant" }?.content

        if (lastUser.isNullOrBlank()) {
            views.setViewVisibility(R.id.widget_last_user, android.view.View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_last_user, android.view.View.VISIBLE)
            views.setTextViewText(R.id.widget_last_user, "Du: $lastUser")
        }

        if (lastReply.isNullOrBlank()) {
            views.setTextViewText(
                R.id.widget_last_reply,
                "Tryk nedenfor for at chatte med Claude"
            )
        } else {
            views.setTextViewText(R.id.widget_last_reply, lastReply)
        }

        // Tryk paa input-bjaelken -> aabn InputActivity
        val inputIntent = Intent(context, InputActivity::class.java)
        val inputPending = PendingIntent.getActivity(
            context, 0, inputIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_input_bar, inputPending)

        // Tryk paa indstillingsikon -> aabn SettingsActivity
        val settingsIntent = Intent(context, SettingsActivity::class.java)
        val settingsPending = PendingIntent.getActivity(
            context, 1, settingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_settings, settingsPending)

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    companion object {
        const val ACTION_REFRESH = "com.claudewidget.app.REFRESH"

        fun refresh(context: Context) {
            val intent = Intent(context, ChatWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            context.sendBroadcast(intent)
        }
    }
}
