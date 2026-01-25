package io.github.sceneview.sample.armodelviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class MonumentAdapter(
    private val monuments: List<Monument>,
    private val onMonumentClick: (Monument) -> Unit
) : RecyclerView.Adapter<MonumentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.monumentImage)
        val name: TextView = view.findViewById(R.id.monumentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monument, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monument = monuments[position]
        holder.name.text = monument.name
        
        if (monument.imageResId != null) {
            holder.image.setImageResource(monument.imageResId)
        } else if (!monument.imageUrl.isNullOrEmpty()) {
            holder.image.load(monument.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.progress_indeterminate_horizontal)
                error(android.R.drawable.stat_notify_error)
            }
        }
        
        holder.itemView.setOnClickListener { onMonumentClick(monument) }
    }

    override fun getItemCount() = monuments.size
}
