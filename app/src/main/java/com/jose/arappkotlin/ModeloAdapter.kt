package com.jose.arappkotlin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ModeloAdapter(private val modelos:List<Modelo>):RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return modelos.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelo = modelos[position]
        holder.bind(modelo)
    }
}