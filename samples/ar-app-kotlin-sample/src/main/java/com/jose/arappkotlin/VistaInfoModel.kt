// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias del framework de Android.
import android.content.Intent // Para crear "intenciones" que permiten iniciar otras actividades.
import android.graphics.BitmapFactory // Para decodificar archivos de imagen en objetos Bitmap.
import android.os.Bundle // Para manejar el estado de la actividad y recibir datos.
import android.util.Log // Para escribir mensajes en el log del sistema (útil para depurar).
import android.widget.Button // Para el componente de botón.
import android.widget.ImageView // Para mostrar imágenes.
import android.widget.TextView // Para mostrar texto.
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades con barra de aplicaciones.
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.io.IOException

// Define la clase para la pantalla de detalles del modelo. Hereda de AppCompatActivity.
class VistaInfoModel : AppCompatActivity() {

    // Este método se llama cuando la actividad se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación de la clase padre. Es obligatorio.
        super.onCreate(savedInstanceState)
        // Establece el layout (la interfaz de usuario) desde el archivo XML `activity_vistas.xml`.
        setContentView(R.layout.activity_vistas)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- Recibir datos de la actividad anterior ---
        // Se obtienen los datos que se pasaron a esta actividad a través del Intent.
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val imagen = intent.getStringExtra("imagen")
        val coordenadas = intent.getStringExtra("coordenadas")
        val modelFileName = intent.getStringExtra("modelFileName")

        // --- Vincular vistas del layout con variables ---
        // Se asignan las vistas del layout a las variables correspondientes para poder manipularlas.
        val imageView: ImageView = findViewById(R.id.imgMonument)
        val textDescrip: TextView = findViewById(R.id.descripcion)
        val btnMap: Button = findViewById(R.id.mapa)
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)

        // Se usa un bloque try-catch para manejar posibles errores, como no encontrar un archivo de imagen.
        try {
            // --- Poblar las vistas con los datos recibidos ---
            // Mostrar la imagen desde la carpeta `assets`.
            val imageStream = assets.open("images/$imagen")
            val bitmap = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(bitmap)

            // Mostrar el nombre y la descripción en los TextViews correspondientes.
            collapsingToolbar.title = nombre
            textDescrip.text = descripcion

            // --- Configurar acciones de los botones y vistas ---
            // Configura el listener para que al hacer clic en la imagen, se abra la vista de Realidad Aumentada.
            imageView.setOnClickListener {
                if (modelFileName != null && modelExists(modelFileName)) {
                    val intent = Intent(this, ActivityModelos::class.java)
                    intent.putExtra("modelFileName", modelFileName)
                    Toast.makeText(this, "Cargando modelo 3D...", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "El modelo 3D no se encontró", Toast.LENGTH_SHORT).show()
                }
            }

            // Configura el listener para que al hacer clic en el botón del mapa, se abra Google Maps.
            btnMap.setOnClickListener {
                // Crea un Intent para iniciar la actividad `Google_Map`.
                val intent = Intent(this, Google_Map::class.java)
                // Añade las coordenadas (como String) al Intent para que la actividad del mapa sepa qué ubicación mostrar.
                intent.putExtra("coordenadas", coordenadas)
                // Inicia la actividad del mapa.
                startActivity(intent)
            }

        } catch (e: Exception) {
            // Si ocurre cualquier error en el bloque `try`, se registra en el log para facilitar la depuración.
            Log.e("VistaInfoModel", "Error en VistaInfoModelo: $e")
        }
    }

    private fun modelExists(fileName: String): Boolean {
        return try {
            assets.open("models/$fileName").close()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
