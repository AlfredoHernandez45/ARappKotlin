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

class Google_Map : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var coordenadas: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        supportActionBar?.hide()



        try {
            //Obtener lo que se va a usar del JSON
             val coordenadas = intent.getStringExtra("coordenadas")
            this.coordenadas = coordenadas.toString()
            //Google Map
            //val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
            createFragment()
        } catch (e: Exception) {
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }

    }
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //this.coordenadas = coordenadas
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    private fun createMarker() {
        val coordenadasArray = coordenadas.split(",")
        val latitude = coordenadasArray[0].toDouble()
        val longitude = coordenadasArray[1].toDouble()
        val coordenadas = LatLng(latitude, longitude)
        val marker: MarkerOptions = MarkerOptions().position(coordenadas)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas, 18f),
            4000,
            null
        )
    }
}