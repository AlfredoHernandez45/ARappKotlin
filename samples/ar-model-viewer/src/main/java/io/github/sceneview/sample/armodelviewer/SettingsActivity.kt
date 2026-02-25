package io.github.sceneview.sample.armodelviewer

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.github.sceneview.sample.setFullScreen

class SettingsActivity : AppCompatActivity(R.layout.activity_settings) {

    private val PREFS_NAME = "ARModelViewerPrefs"
    private val KEY_API_URL = "api_url"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(
            findViewById(android.R.id.content),
            fullScreen = false,
            hideSystemBars = false,
            fitsSystemWindows = true
        )

        val apiUrlEditText = findViewById<TextInputEditText>(R.id.apiUrlEditText)
        val saveButton = findViewById<MaterialButton>(R.id.saveButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentUrl = prefs.getString(KEY_API_URL, "")
        apiUrlEditText.setText(currentUrl)

        saveButton.setOnClickListener {
            val newUrl = apiUrlEditText.text.toString().trim()
            prefs.edit().putString(KEY_API_URL, newUrl).apply()
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
