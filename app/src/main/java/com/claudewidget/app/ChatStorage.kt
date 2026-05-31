package com.claudewidget.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Simpel lokal lagring af samtale, API-nøgle og valgt model.
 * Alt gemmes lokalt på enheden via SharedPreferences.
 */
object ChatStorage {

    private const val PREFS = "claude_widget_prefs"
    private const val KEY_API = "api_key"
    private const val KEY_MODEL = "model"
    private const val KEY_MESSAGES = "messages"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // --- API key ---
    fun getApiKey(context: Context): String =
        prefs(context).getString(KEY_API, "") ?: ""

    fun setApiKey(context: Context, value: String) {
        prefs(context).edit().putString(KEY_API, value).apply()
    }

    // --- Model ---
    fun getModel(context: Context): String =
        prefs(context).getString(KEY_MODEL, "openai")
            ?: "openai"

    fun setModel(context: Context, value: String) {
        prefs(context).edit().putString(KEY_MODEL, value).apply()
    }

    // --- Messages ---
    data class Message(val role: String, val content: String)

    fun getMessages(context: Context): List<Message> {
        val raw = prefs(context).getString(KEY_MESSAGES, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val list = ArrayList<Message>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(Message(o.getString("role"), o.getString("content")))
        }
        return list
    }

    fun addMessage(context: Context, role: String, content: String) {
        val list = getMessages(context).toMutableList()
        list.add(Message(role, content))
        saveMessages(context, list)
    }

    fun saveMessages(context: Context, list: List<Message>) {
        val arr = JSONArray()
        for (m in list) {
            arr.put(JSONObject().put("role", m.role).put("content", m.content))
        }
        prefs(context).edit().putString(KEY_MESSAGES, arr.toString()).apply()
    }

    fun clearMessages(context: Context) {
        prefs(context).edit().putString(KEY_MESSAGES, "[]").apply()
    }
}
