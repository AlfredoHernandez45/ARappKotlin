// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias del framework de Android.
import android.view.LayoutInflater // Se usa para crear objetos View a partir de un archivo de diseño XML.
import android.view.ViewGroup // Es la clase base para los layouts y contenedores de vistas.
import androidx.recyclerview.widget.RecyclerView // La clase principal para mostrar listas y cuadrículas de datos de manera eficiente.

// Define el adaptador para el RecyclerView. Un adaptador es el puente entre los datos y las vistas que los muestran.
// Recibe en su constructor la lista de modelos que va a mostrar.
// Hereda de RecyclerView.Adapter y especifica que usará la clase `ViewHolder` para gestionar las vistas de cada ítem.
class ModeloAdapter(private val modelos: List<Modelo>) : RecyclerView.Adapter<ViewHolder>() {

    // Esta función se llama cuando RecyclerView necesita un nuevo `ViewHolder` para representar un ítem.
    // Esto sucede cuando la lista se muestra por primera vez y cuando el usuario se desplaza y aparecen nuevos ítems en pantalla.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // `parent` es el RecyclerView en el que se mostrará la vista.
        // `viewType` es útil si tienes diferentes tipos de celdas en tu lista, aquí no se usa.

        // Infla (crea) la vista para un solo ítem a partir del archivo de diseño 'item_menu.xml'.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        // `false` indica que no se debe adjuntar la vista al `parent` todavía; RecyclerView lo hará automáticamente.

        // Devuelve un nuevo ViewHolder que contiene la vista del ítem recién creada.
        return ViewHolder(view)
    }

    // Esta función debe devolver el número total de ítems en la lista de datos.
    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista de modelos.
        return modelos.size
    }

    // Esta función se llama para vincular los datos de un ítem específico con su vista (ViewHolder).
    // Se ejecuta para cada ítem visible en la pantalla y cuando te desplazas.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // `holder` es el ViewHolder que se va a reutilizar.
        // `position` es la posición del ítem en la lista de datos.

        // Obtiene el objeto `Modelo` de la lista en la posición actual.
        val modelo = modelos[position]
        // Llama a la función 'bind' del ViewHolder (definida en ViewHolder.kt) para asignar los datos del modelo a la vista.
        // Esto actualiza el contenido de la celda (ej. pone el nombre y la imagen del modelo).
        holder.bind(modelo)
    }
}
