package com.claudewidget.app

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast

/**
 * Indstillinger: vaelg gratis AI-model og ryd samtale.
 * Ingen noegle eller login noedvendig (Pollinations).
 */
class SettingsActivity : Activity() {

    private val models = listOf(
        "openai" to "GPT (anbefalet)",
        "mistral" to "Mistral",
        "llama" to "Llama"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val modelSpinner = findViewById<Spinner>(R.id.settings_model)
        val saveButton = findViewById<Button>(R.id.settings_save)
        val clearButton = findViewById<Button>(R.id.settings_clear)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            models.map { it.second }
        )
        modelSpinner.adapter = adapter

        val currentModel = ChatStorage.getModel(this)
        val currentIndex = models.indexOfFirst { it.first == currentModel }
        if (currentIndex >= 0) modelSpinner.setSelection(currentIndex)

        saveButton.setOnClickListener {
            ChatStorage.setModel(this, models[modelSpinner.selectedItemPosition].first)
            Toast.makeText(this, "Gemt!", Toast.LENGTH_SHORT).show()
            ChatWidgetProvider.refresh(this)
            finish()
        }

        clearButton.setOnClickListener {
            ChatStorage.clearMessages(this)
            ChatWidgetProvider.refresh(this)
            Toast.makeText(this, "Samtale ryddet", Toast.LENGTH_SHORT).show()
        }
    }
}
