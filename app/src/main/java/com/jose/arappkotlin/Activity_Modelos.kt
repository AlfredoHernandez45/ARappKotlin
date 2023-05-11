package com.jose.arappkotlin

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.rendering.HeadlessEngineWrapper
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*

class Activity_Modelos : AppCompatActivity() {

    // Se declara una variable de la clase ArFragment
    // que se utilizará para renderizar el modelo 3D
    private var arCam: ArFragment? = null

    // Se declara una variable que se utilizará para
    // verificar si se ha hecho clic en la pantalla
    // por primera vez
    private var clickNo = 0

    // Función que verifica si ARCore está instalado y actualizado a la última versión
    fun isARCoreSupportedAndUpToDate(): Boolean {
        // Se utiliza una estructura when para manejar
        // los diferentes casos de disponibilidad de ARCore
        return when (ArCoreApk.getInstance().checkAvailability(this)) {
            // Si ARCore está soportado e instalado, retorna true
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
            // Si ARCore está soportado pero la APK es demasiado vieja o no está instalada,
            // se solicita la instalación o actualización de ARCore, y se retorna false
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    when (ArCoreApk.getInstance().requestInstall(this, true)) {
                        // Si se ha solicitado la instalación de ARCore, se retorna false
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            Log.i(HeadlessEngineWrapper.TAG, "ARCore installation requested.")
                            false
                        }
                        // Si ARCore ya está instalado, se retorna true
                        ArCoreApk.InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    // Si se produce un error al verificar la disponibilidad de ARCore, se retorna false
                    Log.e(HeadlessEngineWrapper.TAG, "ARCore not installed", e)
                    false
                }
            }
            // Si el dispositivo no es compatible con ARCore, se retorna false
            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->
                false
            // En caso de error desconocido, se retorna TODO()
            ArCoreApk.Availability.UNKNOWN_ERROR -> TODO()
            // En caso de estar comprobando la disponibilidad de ARCore, se retorna TODO()
            ArCoreApk.Availability.UNKNOWN_CHECKING -> TODO()
            // En caso de agotar el tiempo de espera, se retorna TODO()
            ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> TODO()
        }
    }

    // Función que verifica si el sistema cumple con los requisitos necesarios para poder utilizar ARCore y OpenGL ES 3.0
    fun checkSystemSupport(activity: Activity): Boolean {

        // Verificar si la versión de la API de Android en ejecución es mayor o igual a 24 (Android Nougat 7.0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // Obtener la versión de OpenGL ES del dispositivo
            val openGlVersion =
                (Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))
                        as ActivityManager).deviceConfigurationInfo.glEsVersion

            // Verificar si la versión de OpenGL ES es mayor o igual a 3.0
            if (openGlVersion.toDouble() >= 3.0) {
                // Si se cumple, retornar verdadero
                true
            } else {
                // Si no se cumple, mostrar un mensaje Toast indicando que la versión de OpenGL ES debe ser 3.0 o superior
                // Finalizar la actividad y retornar falso
                Toast.makeText(
                    activity,
                    "App needs OpenGl Version 3.0 or later",
                    Toast.LENGTH_SHORT
                ).show()
                activity.finish()
                false
            }
        } else {
            // Si la versión de la API de Android en ejecución es menor a 24,
            // mostrar un mensaje Toast indicando que la aplicación no es compatible con esa versión
            // Finalizar la actividad y retornar falso
            Toast.makeText(
                activity,
                "App does not support required Build Version",
                Toast.LENGTH_SHORT
            ).show()
            activity.finish()
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelos)

        //Obtenemos el valor extra 'modelo' del Intent
        val modelo = intent.getStringExtra("modelo")

        //Comprobamos si el dispositivo soporta el sistema requerido
        if (isARCoreSupportedAndUpToDate() && checkSystemSupport(this)) {

            //Vinculamos el fragmento ArFragment con su correspondiente id en el archivo de diseño activity_main.xml
            arCam = supportFragmentManager.findFragmentById(R.id.arCameraArea) as ArFragment?

            //Agregamos un oyente de toque a un plano AR detectado
            arCam!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
                clickNo++

                //El modelo 3D solo aparecerá en la escena
                //cuando clickNo sea igual a 1, es decir, solo una vez
                if (clickNo == 1) {
                    //Se crea un anchor a partir del hitResult obtenido
                    val anchor = hitResult.createAnchor()

                    //Se crea el modelo renderizable con la fuente especificada
                    //y se llama a la función 'addModel' para agregarlo a la escena
                    ModelRenderable.builder()
                        .setSource(this,  Uri.parse("models/$modelo"))
                        .setIsFilamentGltf(true)
                        .build()
                        .thenAccept { modelRenderable: ModelRenderable ->
                            addModel(
                                anchor,
                                modelRenderable
                            )
                        }
                        //Si ocurre una excepción, se muestra un diálogo con el mensaje correspondiente
                        .exceptionally { throwable: Throwable ->
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setMessage("Something is not right" + throwable.message).show()
                            null
                        }
                }
            }
        } else {
            //Si el dispositivo no cumple con el sistema requerido, se retorna
            return
        }
    }

    //Aquí se define una función llamada addModel que recibe dos parámetros: anchor y modelRenderable.
    // Este método es utilizado para agregar un modelo 3D a la escena.
    private fun addModel(anchor: Anchor, modelRenderable: ModelRenderable) {

        // Se crea un nuevo AnchorNode que se utilizará para conectar el modelo
        // a un punto específico en la escena. El punto es definido por el anchor.
        val anchorNode = AnchorNode(anchor)

        // Aquí se establece el padre del AnchorNode como la escena
        // que se encuentra dentro del ArFragment (arCam).
        anchorNode.setParent(arCam!!.arSceneView.scene)

        // Se crea un nuevo TransformableNode que será utilizado
        // para controlar la posición, rotación y escala del modelo.
        val model = TransformableNode(arCam!!.transformationSystem)

        // Aquí se establece el padre del modelo como el AnchorNode creado anteriormente.
        model.setParent(anchorNode)

        // Esto asigna el objeto modelRenderable (que es el modelo 3D) al modelo.
        model.renderable = modelRenderable

        // Esto asigna el objeto modelRenderable (que es el modelo 3D) al modelo.
        model.select()
    }

}