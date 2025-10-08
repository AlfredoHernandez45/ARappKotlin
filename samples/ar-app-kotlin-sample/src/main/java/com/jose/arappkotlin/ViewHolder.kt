// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias del framework de Android.
import android.content.Intent // Para crear "intenciones" que permiten iniciar nuevas actividades.
import android.graphics.BitmapFactory // Para decodificar archivos de imagen en objetos Bitmap.
import android.util.Log // Para escribir mensajes en el log del sistema (útil para depurar).
import android.view.View // La clase base para todos los componentes de la interfaz de usuario.
import android.widget.ImageView // Para mostrar imágenes.
import android.widget.TextView // Para mostrar texto.
import androidx.recyclerview.widget.RecyclerView // La clase base para los ViewHolders.

/**
 * ViewHolder: Representa una sola celda (un ítem) en la lista del RecyclerView.
 * Su responsabilidad es mantener las referencias a las vistas dentro de esa celda (como el texto y la imagen)
 * y "vincular" los datos de un objeto (en este caso, un `Modelo`) a esas vistas.
 */
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Guarda una referencia a la ImageView del layout (item_menu.xml) para no tener que buscarla cada vez.
    val imageView: ImageView = itemView.findViewById(R.id.img)
    // Guarda una referencia al TextView del layout.
    val textView: TextView = itemView.findViewById(R.id.titulo)

    // Esta función es llamada por el Adapter para poblar esta celda con los datos de un modelo específico.
    fun bind(modelo: Modelo) {
        try {
            // --- Cargar y mostrar la imagen ---
            // Obtiene el nombre del archivo de la imagen del objeto modelo.
            val imageName = modelo.imagen
            // Abre un flujo de datos (InputStream) para leer la imagen desde la carpeta `assets/images/`.
            val imageStream = itemView.context.assets.open("images/$imageName")
            print(imageStream)
            // Decodifica el flujo de datos en un objeto Bitmap, que es la imagen que se puede mostrar.
            val bitmap = BitmapFactory.decodeStream(imageStream)
            // Establece el Bitmap en la ImageView para que se muestre en la pantalla.
            imageView.setImageBitmap(bitmap)

            // --- Configurar el clic en la imagen ---
            // Agrega un listener que se ejecutará cuando el usuario haga clic en la imagen.
            imageView.setOnClickListener {
                // Crea un Intent para abrir la pantalla de detalles (VistaInfoModel).
                val intent = Intent(itemView.context, VistaInfoModel::class.java)
                // Añade todos los datos del modelo al Intent como "extras" para que la siguiente actividad los reciba.
                intent.putExtra("nombre", modelo.nombre)
                intent.putExtra("descripcion", modelo.descripcion)
                intent.putExtra("imagen", modelo.imagen)
                intent.putExtra("modelFileName", modelo.modelo) // nombre del archivo .glb desde assets
                intent.putExtra("coordenadas", modelo.coordenadas)
                // Inicia la nueva actividad (VistaInfoModel).
                itemView.context.startActivity(intent)
            }

            // --- Cargar y mostrar el texto ---
            // Obtiene el nombre del modelo.
            val nombre = modelo.nombre
            // Establece ese nombre en el TextView.
            textView.text = nombre

            // --- Configurar el clic en el texto ---
            // Agrega un listener que se ejecutará cuando el usuario haga clic en el texto.
            textView.setOnClickListener {
                // Crea un Intent para abrir la pantalla de detalles (VistaInfoModel), igual que con la imagen.
                val intent = Intent(itemView.context, VistaInfoModel::class.java)
                // Añade todos los datos del modelo al Intent.
                intent.putExtra("nombre", modelo.nombre)
                intent.putExtra("descripcion", modelo.descripcion)
                intent.putExtra("imagen", modelo.imagen)
                intent.putExtra("modelo", modelo.modelo)
                intent.putExtra("coordenadas", modelo.coordenadas)
                intent.putExtra("modelFileName", modelo.modelo) // Pass the full asset path
                // Inicia la nueva actividad.
                textView.context.startActivity(intent)
            }

        } catch (e: Exception) {
            // Si ocurre cualquier error en el bloque `try` (ej. la imagen no se encuentra), se registra en el log.
            Log.e("ViewHolder", "Error en ViewHolder: $e")
        }
    }
}
