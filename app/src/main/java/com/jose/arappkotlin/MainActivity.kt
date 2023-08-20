package com.jose.arappkotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.BufferedWriter
import java.io.OutputStreamWriter
/**
 *  Actividad pricipal que se conecta con "activity_main" y se encarga de mostrar
 *  todos los monumentos existentes
 *  */

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Inicializa el RecyclerView
        recyclerView = findViewById(R.id.recyclerView)

        // Abre el archivo JSON "response3.json" que se encuentra en la carpeta "assets"
        val inputStream = assets.open("response3.json")

        // Lee el contenido del archivo JSON y lo almacena en un String llamado "jsonData"
        val jsonData = inputStream.bufferedReader().use { it.readText() }

        // Crea un objeto JSONObject a partir del String "jsonData"
        val jsonRoot = JSONObject(jsonData)

        // Obtiene la lista de objetos JSON llamados "modelos" del objeto JSONObject
        val jsonArray = jsonRoot.getJSONArray("modelos")

        // Inicializa un MutableList de objetos Modelo
        val modelos = mutableListOf<Modelo>()

        try {
            // Itera a través de cada objeto JSON en el JSONArray "modelos"
            for (i in 0 until jsonArray.length()) {

                // Obtiene el objeto JSON actual
                val jsonObject = jsonArray.getJSONObject(i)

                // Crea un objeto Modelo a partir de los valores en el objeto JSON actual
                val modelo = Modelo(
                    nombre = jsonObject.getString("nombre"),
                    descripcion = jsonObject.getString("descripcion"),
                    imagen = jsonObject.getString("imagen"),
                    modelo = jsonObject.getString("modelo"),
                    coordenadas = jsonObject.getString("coordenadas")
                )

                // Agrega el objeto Modelo a la lista "modelos"
                modelos.add(modelo)
            }
        } catch (e: Exception) {
            // Si hay un error al parsear el JSON, registra un mensaje de error en el registro de la aplicación
            Log.e("MainActivity", "Error al parsear JSON: $e")
        }

        // Crea un adaptador ModeloAdapter a partir de la lista de objetos Modelo
        val adapter = ModeloAdapter(modelos)

        // Establece el adaptador en el RecyclerView
        recyclerView.adapter = adapter

        // Establece el GridLayoutManager en el RecyclerView, con dos columnas
        recyclerView.layoutManager = GridLayoutManager(this,2)
    }
}

