package com.jose.arappkotlin.version1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.jose.arappkotlin.R

class menu_Manati : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_principal)
        supportActionBar?.hide()

        val lazaro: ImageButton = findViewById(R.id.mini_lazaro)
        val mujer: ImageButton = findViewById(R.id.mini_mujer)
        val manati: ImageButton = findViewById(R.id.mini_manati)

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

        val btn_manati: ImageView = findViewById(R.id.manati)

        btn_manati.setOnClickListener{
            val intent: Intent = Intent(this, Manati::class.java)
            startActivity(intent)
        }
    }

}