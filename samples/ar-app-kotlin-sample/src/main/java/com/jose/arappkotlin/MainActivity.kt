// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias de Android y otras bibliotecas.
import android.Manifest // Necesario para declarar permisos de la aplicación, como el de la cámara.
import android.content.pm.PackageManager // Proporciona información sobre los paquetes de aplicaciones instalados.
import android.os.Bundle // Utilizado para pasar datos entre actividades de Android.
import android.os.Handler // Permite enviar y procesar objetos Message y Runnable asociados a un hilo.
import android.os.Looper // Utilizado para ejecutar mensajes en un hilo.
import android.util.Log // Utilizado para enviar mensajes de registro al logcat.
import android.widget.Toast // Proporciona una notificación simple al usuario en una pequeña ventana emergente.
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades que utilizan la barra de aplicaciones de la biblioteca de soporte.
import androidx.core.app.ActivityCompat // Ayudante para acceder a funciones en Activity.
import androidx.core.content.ContextCompat // Ayudante para acceder a funciones en Context.
import androidx.recyclerview.widget.GridLayoutManager // Un LayoutManager que organiza los ítems en una cuadrícula.
import androidx.recyclerview.widget.RecyclerView // Un contenedor para mostrar grandes conjuntos de datos que se pueden desplazar eficientemente.
import com.google.ar.core.ArCoreApk // Utilizado para verificar la disponibilidad y solicitar la instalación de ARCore.
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException // Excepción lanzada si el dispositivo no es compatible con ARCore.
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException // Excepción lanzada si el usuario rechaza la instalación de ARCore.
import org.json.JSONObject // Utilizado para analizar datos en formato JSON.

// Define la clase principal de la actividad, que hereda de AppCompatActivity.
class MainActivity : AppCompatActivity() {

    // Un objeto compañero para mantener constantes y métodos estáticos.
    companion object {
        // Etiqueta para los mensajes de registro, facilita la depuración.
        private const val TAG = "ARCoreCheck"
    }

    // Declara una vista RecyclerView que se inicializará más tarde.
    private lateinit var recyclerView: RecyclerView
    // Código de solicitud para el permiso de la cámara.
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    // Un indicador para saber si ya se ha solicitado al usuario la instalación de ARCore.
    private var mUserRequestedInstall = true

    // Este método se llama cuando se crea la actividad por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación de la clase base.
        super.onCreate(savedInstanceState)
        // Establece el diseño de la interfaz de usuario para esta actividad.
        setContentView(R.layout.activity_main)
        // Oculta la barra de acción (ActionBar) si existe.
        supportActionBar?.hide()

        // Inicializa el RecyclerView encontrándolo por su ID en el layout.
        recyclerView = findViewById(R.id.recyclerView)
    }

    // Este método se llama cuando la actividad vuelve a estar en primer plano.
    override fun onResume() {
        // Llama a la implementación de la clase base.
        super.onResume()
        // Verifica la disponibilidad de ARCore en el dispositivo.
        //checkArCoreAvailability()
        checkAndRequestPermissions()
    }

    // Función para verificar si ARCore está disponible e instalado.
    private fun checkArCoreAvailability() {
        try {
            // Obtiene el estado de disponibilidad de ARCore.
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            // Si el estado es transitorio (ej. comprobando), vuelve a intentarlo después de un breve retraso.
            if (availability.isTransient) {
                // Vuelve a consultar en 200 milisegundos.
                Handler(Looper.getMainLooper()).postDelayed({ checkArCoreAvailability() }, 200)
                return
            }
            // Evalúa los diferentes estados de disponibilidad de ARCore.
            when (availability) {
                // Si ARCore está instalado y es compatible.
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                    // Procede a verificar y solicitar los permisos necesarios.
                    checkAndRequestPermissions()
                }
                // Si ARCore es compatible pero no está instalado o la versión es demasiado antigua.
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    try {
                        // Solicita al usuario que instale o actualice ARCore.
                        val installStatus = ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)
                        // Evalúa el resultado de la solicitud de instalación.
                        when (installStatus) {
                            // Si la instalación fue exitosa.
                            ArCoreApk.InstallStatus.INSTALLED -> {
                                // Procede a verificar y solicitar los permisos necesarios.
                                checkAndRequestPermissions()
                            }
                            // Si se ha solicitado la instalación, pero aún no se ha completado.
                            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                                // Se ha solicitado la instalación, no hacer nada más.
                                mUserRequestedInstall = false
                            }
                        }
                    } catch (e: UnavailableUserDeclinedInstallationException) {
                        // Si el usuario rechaza la instalación, muestra un mensaje.
                        Toast.makeText(this, "La instalación de ARCore fue rechazada por el usuario.", Toast.LENGTH_LONG).show()
                        //Aqui mandar a llamar a la actividad principal
                    } catch (e: Exception) {
                        // Si ocurre otro error durante la solicitud, regístralo.
                        Log.e(TAG, "No se pudo solicitar la instalación de ARCore", e)
                    }
                }
                // Si el dispositivo no es compatible con ARCore.
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                    // Muestra un mensaje y cierra la aplicación.
                    Toast.makeText(this, "Este dispositivo no es compatible con AR.", Toast.LENGTH_LONG).show()
                    finish()
                }
                // Si el estado de la comprobación es desconocido o ha fallado.
                ArCoreApk.Availability.UNKNOWN_CHECKING, ArCoreApk.Availability.UNKNOWN_ERROR, ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                    // Muestra un mensaje y cierra la aplicación.
                    Toast.makeText(this, "No se pudo verificar el estado de ARCore.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } catch (e: UnavailableDeviceNotCompatibleException) {
            // Captura la excepción si el dispositivo no es compatible.
            Toast.makeText(this, "Este dispositivo no es compatible con AR.", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            // Captura cualquier otra excepción inesperada con ARCore.
            Log.e(TAG, "Ocurrió un error inesperado con ARCore.", e)
            Toast.makeText(this, "Ocurrió un error inesperado con ARCore.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // Función para verificar y solicitar el permiso de la cámara.
    private fun checkAndRequestPermissions() {
        // Comprueba si el permiso de la cámara no ha sido concedido.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Si no está concedido, solicita el permiso al usuario.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Si el permiso ya está concedido, configura el RecyclerView.
            setupRecyclerView()
        }
    }

    // Este método se llama con el resultado de la solicitud de permisos.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Llama a la implementación de la clase base.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Comprueba si el resultado corresponde a la solicitud del permiso de la cámara.
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Comprueba si se concedió el permiso.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si se concedió, configura el RecyclerView.
                setupRecyclerView()
            } else {
                // Si se denegó, muestra un mensaje al usuario.
                Toast.makeText(this, "El permiso de la cámara es necesario para la Realidad Aumentada.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para configurar el RecyclerView.
    private fun setupRecyclerView() {
        // Abre el archivo JSON desde la carpeta de assets.
        val inputStream = assets.open("response3.json")
        // Lee el contenido del archivo en una cadena de texto.
        val jsonData = inputStream.bufferedReader().use { it.readText() }
        // Parsea la cadena de texto JSON a un objeto JSONObject.
        val jsonRoot = JSONObject(jsonData)
        // Obtiene el array de "modelos" del objeto JSON.
        val jsonArray = jsonRoot.getJSONArray("modelos")
        // Crea una lista mutable para almacenar los objetos de tipo Modelo.
        val modelos = mutableListOf<Modelo>()

        try {
            // Itera sobre cada elemento del array JSON.
            for (i in 0 until jsonArray.length()) {
                // Obtiene el objeto JSON en la posición actual.
                val jsonObject = jsonArray.getJSONObject(i)
                // Obtiene el nombre del archivo del modelo 3D.
                val modelName = jsonObject.optString("modelo")
                // Obtiene el ID del recurso del modelo 3D desde la carpeta "raw".
                val modelResourceId = resources.getIdentifier(modelName.substringBefore("."), "raw", packageName)

                // Crea una instancia de la clase Modelo con los datos del JSON.
                val modelo = Modelo(
                    nombre = jsonObject.optString("nombre"),
                    descripcion = jsonObject.optString("descripcion"),
                    imagen = jsonObject.optString("imagen"),
                    modelo = modelName,
                    coordenadas = jsonObject.optString("coordenadas"),
                    modelResourceId = modelResourceId
                )
                // Añade el objeto Modelo a la lista.
                modelos.add(modelo)
            }
        } catch (e: Exception) {
            // Si ocurre un error al parsear el JSON, lo registra en el log.
            Log.e("MainActivity", "Error parseando JSON: $e")
        }

        // Crea una instancia del adaptador para el RecyclerView, pasándole la lista de modelos.
        val adapter = ModeloAdapter(modelos)
        // Establece el adaptador en el RecyclerView.
        recyclerView.adapter = adapter
        // Establece un GridLayoutManager con 2 columnas para el RecyclerView.
        recyclerView.layoutManager = GridLayoutManager(this,2)
    }
}