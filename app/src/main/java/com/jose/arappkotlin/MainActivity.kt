package com.jose.arappkotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        recyclerView = findViewById(R.id.recyclerView)

        val jsonData = resources.openRawResource(R.raw.response3).bufferedReader().use { it.readText() }
        val jsonRoot = JSONObject(jsonData)
        val jsonArray = jsonRoot.getJSONArray("modelos")
        val modelos = mutableListOf<Modelo>()
        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val modelo = Modelo(
                    nombre = jsonObject.getString("nombre"),
                    descripcion = jsonObject.getString("descripcion"),
                    imagen = jsonObject.getString("imagen"),
                    modelo = jsonObject.getString("modelo"),
                    coordenadas = jsonObject.getString("coordenadas")
                )
                modelos.add(modelo)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al parsear JSON: $e")
        }
        val adapter = ModeloAdapter(modelos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this,2)
    }

    private fun validacionVersiones() {}


}