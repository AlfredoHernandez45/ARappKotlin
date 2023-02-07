package com.jose.arappkotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageView: ImageView = itemView.findViewById(R.id.img)

    fun bind(modelo: Modelo) {

        try {
            //Obtener y mostrar imagenes
            val image = modelo.imagen
            val imageStream = itemView.context.assets.open("images/$image")
            val bitmap = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(bitmap)

            imageView.setOnClickListener {
                val intent = Intent(itemView.context, VistaInfoModel::class.java)
                intent.putExtra("nombre", modelo.nombre)
                intent.putExtra("descripcion", modelo.descripcion)
                intent.putExtra("imagen", modelo.imagen)
                intent.putExtra("modelo", modelo.modelo)
                itemView.context.startActivity(intent)
            }
        }catch (e: Exception) {
            Log.e("ViewHolder", "Error en ViewHolder: $e")
        }

    }

}