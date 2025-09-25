// Define el paquete al que pertenece esta interfaz.
package com.jose.arappkotlin

// Importa la anotación GET de la biblioteca Retrofit.
import retrofit2.http.GET

// Define una interfaz que Retrofit utilizará para crear las llamadas a la API.
interface Api {
    // Anotación que indica que esta función realizará una petición HTTP GET.
    @GET("modelos") // Especifica el "endpoint" o la ruta relativa de la API a la que se llamará.
    // Declara una función de suspensión (para ser usada con corutinas) llamada getModelos.
    suspend fun getModelos(): List<Modelo>
    // Esta función devolverá una lista de objetos "Modelo", que Retrofit parseará automáticamente desde la respuesta JSON.
}