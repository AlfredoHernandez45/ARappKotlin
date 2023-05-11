package com.jose.arappkotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ModeloAdapter(private val modelos: List<Modelo>) : RecyclerView.Adapter<ViewHolder>() {

    // Esta función se llama cuando se crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Se infla el archivo XML de diseño 'item_menu' y se convierte en una vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        // Se devuelve un nuevo ViewHolder que contiene la vista creada
        return ViewHolder(view)
    }

    // Esta función devuelve el número de elementos en la lista de modelos
    override fun getItemCount(): Int {
        return modelos.size
    }

    // Esta función se llama cuando se debe asignar un modelo a un ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Se obtiene el modelo en la posición actual
        val modelo = modelos[position]
        // Se llama a la función 'bind' del ViewHolder para asignar los datos del modelo a la vista
        holder.bind(modelo)
    }
}
