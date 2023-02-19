package com.jose.arappkotlin.version1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.jose.arappkotlin.R

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar?.hide()

        val btn_manati: ImageButton = findViewById(R.id.manati)
        val btn_lazaro: ImageView = findViewById(R.id.lazaro)
        val btn_leona: ImageView = findViewById(R.id.mujer)

        btn_manati.setOnClickListener{
            val intent: Intent = Intent(this, Manati::class.java)
            startActivity(intent)
        }
        btn_lazaro.setOnClickListener{
            val intent: Intent = Intent(this, Lazaro::class.java)
            startActivity(intent)
        }
        btn_leona.setOnClickListener {
            val intent: Intent = Intent(this, Mujer::class.java)
            startActivity(intent)
        }

    }

}