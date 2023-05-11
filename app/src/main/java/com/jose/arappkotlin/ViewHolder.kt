package com.jose.arappkotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Se inicializa una ImageView a través de su identificador en el archivo de diseño XML
    val imageView: ImageView = itemView.findViewById(R.id.img)

    // Esta función asigna los datos del modelo a la vista del ViewHolder
    fun bind(modelo: Modelo) {
        try {
            // Obtener y mostrar imágenes
            // Se obtiene el nombre de la imagen del modelo
            val image = modelo.imagen
            // Se abre un flujo de entrada de assets para la imagen
            val imageStream = itemView.context.assets.open("images/$image")
            // Se decodifica el flujo de entrada de la imagen en un Bitmap
            val bitmap = BitmapFactory.decodeStream(imageStream)
            // Se establece el Bitmap en la ImageView
            imageView.setImageBitmap(bitmap)

            // Se agrega un listener a la ImageView para cuando se haga clic en la imagen
            imageView.setOnClickListener {
                // Se crea un intent para mostrar la información detallada del modelo
                val intent = Intent(itemView.context, VistaInfoModel::class.java)
                // Se agregan datos adicionales al intent, como el nombre, descripción, imagen, modelo y coordenadas del modelo
                intent.putExtra("nombre", modelo.nombre)
                intent.putExtra("descripcion", modelo.descripcion)
                intent.putExtra("imagen", modelo.imagen)
                intent.putExtra("modelo", modelo.modelo)
                intent.putExtra("coordenadas", modelo.coordenadas)
                // Se inicia una nueva actividad con el intent creado
                itemView.context.startActivity(intent)
            }
        } catch (e: Exception) {
            // Se registra cualquier error en el log
            Log.e("ViewHolder", "Error en ViewHolder: $e")
        }
    }
}
