package com.claudewidget.app

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Lille dialog der popper op naar man trykker paa widgetten.
 * Bruger gratis AI (Pollinations) - ingen noegle noedvendig.
 */
class InputActivity : Activity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val editText = findViewById<EditText>(R.id.input_text)
        val sendButton = findViewById<Button>(R.id.input_send)
        val cancelButton = findViewById<Button>(R.id.input_cancel)

        editText.requestFocus()
        cancelButton.setOnClickListener { finish() }

        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isEmpty()) {
                finish()
                return@setOnClickListener
            }
            sendButton.isEnabled = false
            sendButton.text = "Sender..."
            sendMessage(text)
        }
    }

    private fun sendMessage(text: String) {
        ChatStorage.addMessage(this, "user", text)
        ChatWidgetProvider.refresh(this)

        val model = ChatStorage.getModel(this)
        val history = ChatStorage.getMessages(this)

        scope.launch {
            try {
                val reply = withContext(Dispatchers.IO) {
                    ClaudeApi.sendMessage(model, history)
                }
                ChatStorage.addMessage(this@InputActivity, "assistant", reply)
            } catch (e: Exception) {
                ChatStorage.addMessage(
                    this@InputActivity,
                    "assistant",
                    "Fejl: ${e.message}"
                )
            } finally {
                ChatWidgetProvider.refresh(this@InputActivity)
                finish()
            }
        }
    }
}
