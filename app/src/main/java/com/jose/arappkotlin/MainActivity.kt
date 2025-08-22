package com.jose.arappkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recyclerView)

        // 1. Comprobar y solicitar permisos al iniciar
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permiso no concedido, solicitarlo.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Permiso ya concedido, configurar la UI.
            setupRecyclerView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido por el usuario.
                setupRecyclerView()
            } else {
                // Permiso denegado. Informar al usuario.
                Toast.makeText(this, "El permiso de cámara es necesario para la función de Realidad Aumentada", Toast.LENGTH_LONG).show()
                // Opcional: podrías deshabilitar botones o cerrar la app si es estrictamente necesaria.
            }
        }
    }

    private fun setupRecyclerView() {
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
                    nombre = jsonObject.optString("nombre"),
                    descripcion = jsonObject.optString("descripcion"),
                    imagen = jsonObject.optString("imagen"),
                    modelo = jsonObject.optString("modelo"),
                    coordenadas = jsonObject.optString("coordenadas")
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