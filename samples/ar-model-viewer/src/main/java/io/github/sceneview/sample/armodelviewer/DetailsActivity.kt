package io.github.sceneview.sample.armodelviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import io.github.sceneview.sample.setFullScreen

class DetailsActivity : AppCompatActivity(R.layout.activity_details) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(
            findViewById(R.id.imageCard),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        val monument = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_MONUMENT", Monument::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_MONUMENT")
        } ?: return

        findViewById<TextView>(R.id.monumentTitle).text = monument.name
        findViewById<TextView>(R.id.monumentDescription).text = monument.description
        val imageView = findViewById<ImageView>(R.id.monumentDetailsImage)
        
        if (monument.imageResId != null) {
            imageView.setImageResource(monument.imageResId)
        } else if (!monument.imageUrl.isNullOrEmpty()) {
            imageView.load(monument.imageUrl) {
                crossfade(true)
            }
        }

        imageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                if (monument.modelResId != null) {
                    putExtra("EXTRA_MODEL_RES_ID", monument.modelResId)
                } else if (!monument.modelUrl.isNullOrEmpty()) {
                    putExtra("EXTRA_MODEL_URL", monument.modelUrl)
                }
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnHowToGetThere).setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${monument.latitude},${monument.longitude}?q=${monument.latitude},${monument.longitude}(${monument.name})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }
}
