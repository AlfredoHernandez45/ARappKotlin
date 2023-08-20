package com.jose.arappkotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Este esta encargado de obtener toda la información de los modelos y mostrarlo
 * */
class VistaInfoModel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vistas)
        supportActionBar?.hide()

        //Se obtienen los datos que se pasan a esta actividad a través de un Intent
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val imagen = intent.getStringExtra("imagen")
        val modelo = intent.getStringExtra("modelo")
        val coordenadas = intent.getStringExtra("coordenadas")

        //Se asignan las vistas del layout a las variables correspondientes
        val imageView: ImageView = findViewById(R.id.imgMonument)
        val textTitulo: TextView = findViewById(R.id.titulo)
        val textDescrip: TextView = findViewById(R.id.descripcion)
        val btnMap: Button = findViewById(R.id.mapa)

        try {
            //Se muestra la imagen correspondiente en el ImageView a partir del archivo almacenado en assets
            val imageStream = assets.open("images/$imagen")
            val bitmap = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(bitmap)

            //Se asignan los valores de nombre y descripción a los TextView correspondientes
            textTitulo.text = nombre
            textDescrip.text = descripcion

            //Se abre la actividad Activity_Modelos cuando se hace click en la imagen
            imageView.setOnClickListener {
                val intent = Intent(this, Activity_Modelos::class.java)
                intent.putExtra("modelo", modelo)
                startActivity(intent)
            }

            //Se abre la actividad Google_Map cuando se hace click en el botón
            btnMap.setOnClickListener {
                val intent = Intent(this, Google_Map::class.java)
                intent.putExtra("coordenadas", coordenadas)
                startActivity(intent)
            }

        }catch (e: Exception) {
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }
    }
}

