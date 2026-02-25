package io.github.sceneview.sample.armodelviewer

import android.content.Context
import fuel.Fuel

import fuel.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray

@Serializable
data class ApiProduct(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val modelo: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

object SupabaseApi {
    private const val DEFAULT_BASE_URL = "https://buhtjjlxsgpclkdnmilf.supabase.co/rest/v1/productos"
    // Use the Anon key provided in environment.ts
    private const val SUPABASE_KEY = "sb_publishable_YRcJCGFxzmuad2hcX0XwMw_forgrOtD"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchMonuments(context: android.content.Context): List<Monument> = withContext(Dispatchers.IO) {
        try {
            val prefs = context.getSharedPreferences("ARModelViewerPrefs", Context.MODE_PRIVATE)
            val storedUrl = prefs.getString("api_url", "")
            val baseUrl = if (!storedUrl.isNullOrEmpty()) storedUrl else DEFAULT_BASE_URL

            val loader = Fuel.loader()
            val response = loader.get {
                url = baseUrl
                headers = mapOf(
                    "apikey" to SUPABASE_KEY,
                    "Authorization" to "Bearer $SUPABASE_KEY"
                )
            }
            
            if (response.statusCode == 200) {
                // In Fuel 3, we read from response.source using kotlinx-io
                val responseBody = response.source.readByteArray().decodeToString()
                val apiProducts = json.decodeFromString<List<ApiProduct>>(responseBody)
                apiProducts.map { product ->
                    Monument(
                        id = product.id,
                        name = product.nombre,
                        description = product.descripcion,
                        imageUrl = product.imagen,
                        modelUrl = product.modelo,
                        latitude = product.latitude,
                        longitude = product.longitude
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
