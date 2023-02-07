package com.jose.arappkotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VistaInfoModel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vistas)
        supportActionBar?.hide()

        //Obtener lo que se va a usar del JSON
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val imagen = intent.getStringExtra("imagen")
        val modelo = intent.getStringExtra("modelo")


        val imageView: ImageView = findViewById(R.id.imgEjemplo)
        val textTitulo: TextView = findViewById(R.id.titulo)
        val textDescrip: TextView = findViewById(R.id.descripcion)

        try {

            // Mostrar imagenes
            val imageStream = assets.open("images/$imagen")
            val bitmap = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(bitmap)

            //Mostrar informacion
            textTitulo.text = nombre
            textDescrip.text = descripcion

            imageView.setOnClickListener {
                val intent = Intent(this, Activity_Modelos::class.java)
                intent.putExtra("modelo", modelo)
                startActivity(intent)
            }

        }catch (e: Exception) {
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }

    }
}