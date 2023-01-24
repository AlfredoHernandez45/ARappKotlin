package com.jose.arappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class menu_Mujer : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_mujer)
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

        val btn_lazaro: ImageView = findViewById(R.id.mujer)

        btn_lazaro.setOnClickListener{
            val intent: Intent = Intent(this, Mujer::class.java)
            startActivity(intent)
        }
    }
}