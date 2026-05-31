package com.claudewidget.app

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Gratis AI via Pollinations.ai - INGEN noegle eller login noedvendig.
 * OpenAI-kompatibel endpoint.
 */
object ClaudeApi {

    private const val URL = "https://text.pollinations.ai/openai"
    private val JSON = "application/json".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    fun sendMessage(
        model: String,
        messages: List<ChatStorage.Message>
    ): String {
        val msgArray = JSONArray()
        for (m in messages) {
            msgArray.put(JSONObject().put("role", m.role).put("content", m.content))
        }

        val body = JSONObject()
            .put("model", model)
            .put("messages", msgArray)
            .toString()

        val request = Request.Builder()
            .url(URL)
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody(JSON))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}")
            }

            // OpenAI-kompatibelt svar
            try {
                val json = JSONObject(responseBody)
                val choices = json.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val msg = choices.getJSONObject(0).optJSONObject("message")
                    if (msg != null) return msg.getString("content").trim()
                }
            } catch (e: Exception) {
                // Falder igennem til ren tekst nedenfor
            }

            // Nogle svar er ren tekst
            return responseBody.trim()
        }
    }
}
