package com.jose.arappkotlin

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*

class menu_Lazaro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_lazaro)
        supportActionBar?.hide()

        val lazaro: ImageView = findViewById(R.id.mini_lazaro)
        val mujer: ImageView = findViewById(R.id.mini_mujer)
        val manati: ImageView = findViewById(R.id.mini_manati)

        lazaro.setOnClickListener{
            val intent: Intent = Intent(this, menu_Lazaro::class.java)
            startActivity(intent)
        }
        mujer.setOnClickListener{
            val intent: Intent = Intent(this, menu_Mujer::class.java)
            startActivity(intent)
        }
        manati.setOnClickListener{
            val intent: Intent = Intent(this, menu_Manati::class.java)
            startActivity(intent)
        }

        val btn_lazaro: ImageView = findViewById(R.id.lazaro)

        btn_lazaro.setOnClickListener{
            val intent: Intent = Intent(this, Lazaro::class.java)
            startActivity(intent)
        }
    }

}