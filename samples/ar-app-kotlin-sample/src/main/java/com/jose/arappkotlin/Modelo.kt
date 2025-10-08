// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Declara una "data class" llamada Modelo.
// Las "data class" en Kotlin son clases concisas diseñadas específicamente para contener datos.
// El compilador genera automáticamente métodos útiles como toString(), equals(), hashCode(), copy(), etc.
data class Modelo(
    // Propiedad inmutable para el nombre del modelo (ej. "Silla de oficina"). Se usará para mostrar en la lista.
    val nombre: String,
    // Propiedad para la descripción detallada del modelo.
    val descripcion: String,
    // Propiedad para la URL o el nombre del archivo de la imagen de vista previa del modelo.
    val imagen: String,
    // Propiedad para el nombre del archivo del modelo 3D (ej. "silla.glb").
    val modelo: String,
    // Propiedad para las coordenadas geográficas (latitud,longitud) asociadas a este modelo, guardadas como un String.
    val coordenadas: String
)
