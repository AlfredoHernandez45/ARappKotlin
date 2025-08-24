package com.jose.arappkotlin

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "ARAppPrefs"
        private const val ARCORE_INSTALL_REQUESTED = "arcore_install_requested"
        private const val TAG = "ARCoreCheck"
    }

    private lateinit var recyclerView: RecyclerView
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private var isArCoreReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recyclerView)
    }

    override fun onResume() {
        super.onResume()
        // Llama directamente a la verificación de permisos, ya que la de ARCore está omitida.
        checkAndRequestPermissions()
    }

    private fun checkAndRequestArCore() {
        try {
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            Log.d(TAG, "ARCore Availability: $availability")

            if (availability.isTransient) {
                Handler(Looper.getMainLooper()).postDelayed({ checkAndRequestArCore() }, 200)
                return
            }

            when (availability) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                    isArCoreReady = true
                    checkAndRequestPermissions()
                }
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    try {
                        ArCoreApk.getInstance().requestInstall(this, true)
                    } catch (e: UnavailableUserDeclinedInstallationException) {
                        // El usuario denegó la instalación. No hacer nada para evitar bucles o cierres.
                        Toast.makeText(this, "ARCore installation was declined by the user.", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to request ARCore installation", e)
                    }
                }
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                    Toast.makeText(this, "Este dispositivo no es compatible con AR", Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this, "No se pudo verificar el estado de ARCore.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } catch (e: UnavailableDeviceNotCompatibleException) {
            Toast.makeText(this, "Este dispositivo no es compatible con AR", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al verificar ARCore", e)
            Toast.makeText(this, "Ocurrió un error inesperado con ARCore.", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupRecyclerView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRecyclerView()
            } else {
                Toast.makeText(this, "El permiso de cámara es necesario para la función de Realidad Aumentada", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val inputStream = assets.open("response3.json")
        val jsonData = inputStream.bufferedReader().use { it.readText() }
        val jsonRoot = JSONObject(jsonData)
        val jsonArray = jsonRoot.getJSONArray("modelos")
        val modelos = mutableListOf<Modelo>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val modelo = Modelo(
                    nombre = jsonObject.optString("nombre"),
                    descripcion = jsonObject.optString("descripcion"),
                    imagen = jsonObject.optString("imagen"),
                    modelo = jsonObject.optString("modelo"),
                    coordenadas = jsonObject.optString("coordenadas")
                )
                modelos.add(modelo)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al parsear JSON: $e")
        }

        val adapter = ModeloAdapter(modelos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this,2)
    }
}