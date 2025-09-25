// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias de Android y Google Maps.
import android.os.Bundle // Para manejar el estado de la actividad.
import android.util.Log // Para registrar mensajes de depuración y error.
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades con barra de aplicaciones.
import com.google.android.gms.maps.CameraUpdateFactory // Para crear actualizaciones de la cámara del mapa (mover, zoom, etc.).
import com.google.android.gms.maps.GoogleMap // El objeto principal que representa el mapa.
import com.google.android.gms.maps.OnMapReadyCallback // Interfaz para recibir una notificación cuando el mapa está listo.
import com.google.android.gms.maps.SupportMapFragment // Un fragmento que muestra un mapa de Google.
import com.google.android.gms.maps.model.LatLng // Representa una coordenada geográfica (latitud y longitud).
import com.google.android.gms.maps.model.MarkerOptions // Para configurar las opciones de un marcador en el mapa.

// Se define una clase llamada Google_Map que hereda de AppCompatActivity e implementa la interfaz OnMapReadyCallback.
// AppCompatActivity le da la funcionalidad de una pantalla de Android.
// OnMapReadyCallback obliga a implementar el método onMapReady, que se llama cuando el mapa está listo.
class Google_Map : AppCompatActivity(), OnMapReadyCallback {

    // Declara una variable para el objeto GoogleMap. `lateinit` indica que se inicializará más tarde.
    private lateinit var map: GoogleMap
    // Declara una variable para almacenar las coordenadas (como texto) que se mostrarán en el mapa.
    private lateinit var coordenadas: String

    // Esta función se llama al crear la instancia de la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación de la clase padre. Es obligatorio.
        super.onCreate(savedInstanceState)

        // Establece el layout (la interfaz de usuario) desde el archivo XML.
        setContentView(R.layout.activity_google_map)

        // Oculta la barra de acción (la barra superior) para dar más espacio al mapa.
        supportActionBar?.hide()

        // Se intenta obtener las coordenadas del Intent que inició esta actividad.
        try {
            // Recupera el string con la clave "coordenadas" del Intent.
            val coordenadas = intent.getStringExtra("coordenadas")
            // Asigna el valor recuperado a la variable de la clase.
            this.coordenadas = coordenadas.toString()
            // Llama a la función para configurar el fragmento del mapa.
            createFragment()
        } catch (e: Exception) {
            // Si algo falla (ej. no se pasaron las coordenadas), se registra un error.
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }
    }

    // Esta función se utiliza para crear el fragmento de mapa y mostrarlo en la actividad.
    private fun createFragment() {
        // Busca el fragmento de mapa en el layout y lo convierte a SupportMapFragment.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        // Registra un callback para notificar cuando el mapa esté listo para ser utilizado.
        // `this` indica que el callback (onMapReady) está en esta misma clase.
        mapFragment.getMapAsync(this)
    }

    // Esta función (callback) se llama cuando el mapa está listo para ser utilizado.
    override fun onMapReady(googleMap: GoogleMap) {
        // Guarda una referencia a la instancia de GoogleMap en la propiedad `map`.
        map = googleMap
        // Llama a la función para agregar un marcador en el mapa ya cargado.
        createMarker()
    }

    // Esta función se utiliza para crear un marcador en el mapa.
    private fun createMarker() {
        // Divide el string de coordenadas (ej: "19.43,-99.13") en un array usando la coma como separador.
        val coordenadasArray = coordenadas.split(",")
        // Convierte la primera parte (latitud) a un número de tipo Double.
        val latitude = coordenadasArray[0].toDouble()
        // Convierte la segunda parte (longitud) a un número de tipo Double.
        val longitude = coordenadasArray[1].toDouble()
        // Crea un objeto LatLng con las coordenadas numéricas.
        val coordenadasLatLng = LatLng(latitude, longitude)
        // Crea un objeto MarkerOptions para definir la posición del marcador.
        val marker: MarkerOptions = MarkerOptions().position(coordenadasLatLng)
        // Agrega el marcador al mapa.
        map.addMarker(marker)
        // Anima la cámara para que se mueva y haga zoom a la ubicación del marcador.
        map.animateCamera(
            // Crea una actualización de cámara para centrarla en las coordenadas con un nivel de zoom de 18f.
            CameraUpdateFactory.newLatLngZoom(coordenadasLatLng, 18f),
            // Duración de la animación en milisegundos (4 segundos).
            4000,
            // Callback opcional para cuando la animación termina (en este caso, ninguno).
            null
        )
    }
}
