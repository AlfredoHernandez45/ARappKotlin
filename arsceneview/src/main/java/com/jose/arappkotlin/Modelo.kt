package com.jose.arappkotlin

// Declara una clase de datos Modelo para representar objetos que contienen información sobre un modelo
data class Modelo(
    val nombre: String,         // Representa el nombre del modelo
    val descripcion: String,    // Representa la descripción del modelo
    val imagen: String,         // Representa la ruta de la imagen asociada al modelo
    val modelo: String,         // Representa la ruta del modelo 3D asociado al modelo
    val coordenadas: String     // Representa las coordenadas en las que se encuentra el modelo
)
