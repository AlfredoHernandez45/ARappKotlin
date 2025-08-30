package com.jose.arappkotlin

import android.Manifest
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
        private const val TAG = "ARCoreCheck"
    }

    private lateinit var recyclerView: RecyclerView
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private var mUserRequestedInstall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recyclerView)
    }

    override fun onResume() {
        super.onResume()
        checkArCoreAvailability()
    }

    private fun checkArCoreAvailability() {
        try {
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            if (availability.isTransient) {
                // Re-query in a few seconds.
                Handler(Looper.getMainLooper()).postDelayed({ checkArCoreAvailability() }, 200)
                return
            }
            when (availability) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                    // ARCore is installed and ready.
                    checkAndRequestPermissions()
                }
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    try {
                        // Request ARCore installation or update.
                        val installStatus = ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)
                        when (installStatus) {
                            ArCoreApk.InstallStatus.INSTALLED -> {
                                // Success, ARCore is installed.
                                checkAndRequestPermissions()
                            }
                            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                                // Installation is requested, don't do anything further.
                                mUserRequestedInstall = false
                            }
                        }
                    } catch (e: UnavailableUserDeclinedInstallationException) {
                        Toast.makeText(this, "ARCore installation was declined by the user.", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to request ARCore installation", e)
                    }
                }
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                    Toast.makeText(this, "This device is not compatible with AR.", Toast.LENGTH_LONG).show()
                    finish()
                }
                ArCoreApk.Availability.UNKNOWN_CHECKING, ArCoreApk.Availability.UNKNOWN_ERROR, ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                    Toast.makeText(this, "Could not check ARCore status.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } catch (e: UnavailableDeviceNotCompatibleException) {
            Toast.makeText(this, "This device is not compatible with AR.", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred with ARCore.", e)
            Toast.makeText(this, "An unexpected error occurred with ARCore.", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Camera permission is required for Augmented Reality.", Toast.LENGTH_LONG).show()
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
                val modelName = jsonObject.optString("modelo")
                val modelResourceId = resources.getIdentifier(modelName.substringBefore("."), "raw", packageName)

                val modelo = Modelo(
                    nombre = jsonObject.optString("nombre"),
                    descripcion = jsonObject.optString("descripcion"),
                    imagen = jsonObject.optString("imagen"),
                    modelo = modelName,
                    coordenadas = jsonObject.optString("coordenadas"),
                    modelResourceId = modelResourceId
                )
                modelos.add(modelo)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error parsing JSON: $e")
        }

        val adapter = ModeloAdapter(modelos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this,2)
    }
}