package com.jose.arappkotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// Se define una clase llamada Google_Map que extiende la clase AppCompatActivity e implementa la interfaz OnMapReadyCallback
class Google_Map : AppCompatActivity(), OnMapReadyCallback {

    // Se definen dos propiedades privadas de la clase
    private lateinit var map: GoogleMap // Variable para almacenar una instancia de GoogleMap
    private lateinit var coordenadas: String // Variable para almacenar las coordenadas del lugar que se va a mostrar en el mapa

    // Esta función se llama al crear la instancia de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se establece el layout de la actividad
        setContentView(R.layout.activity_google_map)

        // Se oculta la barra de acción
        supportActionBar?.hide()

        // Se intenta obtener las coordenadas del lugar desde el Intent que inició la actividad
        try {
            val coordenadas = intent.getStringExtra("coordenadas")
            this.coordenadas = coordenadas.toString()
            createFragment()
        } catch (e: Exception) {
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }
    }

    // Esta función se utiliza para crear el fragmento de mapa y mostrarlo en la actividad
    private fun createFragment() {
        // Se busca el fragmento de mapa en el layout de la actividad y se obtiene una instancia de SupportMapFragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        // Se llama a la función getMapAsync() del fragmento para notificar cuando el mapa está listo para ser utilizado
        mapFragment.getMapAsync(this)
    }

    // Esta función se llama cuando el mapa está listo para ser utilizado
    override fun onMapReady(googleMap: GoogleMap) {
        // Se guarda una referencia a la instancia de GoogleMap en la propiedad map
        map = googleMap
        // Se llama a la función createMarker() para agregar un marcador en el mapa
        createMarker()
    }

    // Esta función se utiliza para crear un marcador en el mapa
    private fun createMarker() {
        // Se dividen las coordenadas en dos valores: latitud y longitud
        val coordenadasArray = coordenadas.split(",")
        val latitude = coordenadasArray[0].toDouble()
        val longitude = coordenadasArray[1].toDouble()
        // Se crea un objeto LatLng con las coordenadas
        val coordenadas = LatLng(latitude, longitude)
        // Se crea un objeto MarkerOptions con la posición del marcador
        val marker: MarkerOptions = MarkerOptions().position(coordenadas)
        // Se agrega el marcador al mapa
        map.addMarker(marker)
        // Se anima la cámara para que muestre la ubicación del marcador
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas, 18f),
            4000,
            null
        )
    }
}
