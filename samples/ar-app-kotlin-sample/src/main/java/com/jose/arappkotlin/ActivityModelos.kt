// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias de Android y otras bibliotecas.
import android.os.Bundle // Utilizado para pasar datos entre actividades y guardar el estado de la aplicación.
import android.util.Log // Herramienta para enviar mensajes de registro al sistema (logcat).
import android.view.View
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades que usan la barra de aplicaciones de compatibilidad.
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
import java.io.IOException

// Actividad responsable de mostrar modelos 3D en una escena.
class ActivityModelos : AppCompatActivity(R.layout.activity_modelos) {
    private lateinit var sceneView: SceneView
    private lateinit var loadingView: View
    private var modelNode: ModelNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sceneView = findViewById(R.id.sceneView)
        loadingView = findViewById(R.id.loadingView)

        val modelFileName = intent.getStringExtra("modelFileName")

        if (modelFileName == null) {
            Log.e("ActivityModelos", "No se recibió nombre de archivo del modelo.")
            finish()
            return
        }

        lifecycleScope.launch {
            loadingView.isGone = false
            val hdrFile = "environments/studio_small_09_2k.hdr"
            sceneView.environmentLoader.loadHDREnvironment(hdrFile)?.let {
                sceneView.indirectLight = it.indirectLight
                sceneView.skybox = it.skybox
            }

            val modelFile = "models/manati_4.glb"
            try {
                val modelInstance = sceneView.modelLoader.createModelInstance(modelFile)
                modelNode = ModelNode(modelInstance).apply {
                    scaleToUnitCube(0.5f)
                }
                sceneView.addChildNode(modelNode!!)
            } catch (e: IOException) {
                Log.e("ActivityModelos", "No se pudo cargar el modelo: $modelFile", e)
            }

            sceneView.cameraNode.position = Position(z = 4.0f)

            loadingView.isGone = true
            Log.d("ActivityModelos", "Modelo cargado y centrado: $modelFile")
        }
    }
}
