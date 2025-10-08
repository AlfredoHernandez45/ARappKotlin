package com.jose.arappkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class ActivityModelos : AppCompatActivity() {

    // Vistas de la interfaz de usuario
    private lateinit var sceneView: ARSceneView
    private lateinit var loadingView: View
    private lateinit var instructionText: TextView

    // Nodo que contiene el modelo 3D anclado al mundo real
    private var anchorNode: AnchorNode? = null

    // Gestiona la visibilidad del indicador de carga
    private var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
        }

    // Almacena la razón del fallo de seguimiento para mostrar instrucciones
    private var trackingFailureReason: TrackingFailureReason? = null
        set(value) {
            if (field != value) {
                field = value
                updateInstructions()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelos)

        // Inicialización de las vistas
        sceneView = findViewById(R.id.sceneView)
        loadingView = findViewById(R.id.loadingView)
        instructionText = findViewById(R.id.instructionText)

        // Configuración de la escena AR
        setupSceneView()
    }

    private fun setupSceneView() {
        sceneView.apply {
            lifecycle = this@ActivityModelos.lifecycle
            planeRenderer.isEnabled = true

            configureSession { session, config -> // session aquí es de tipo ARSession? (nullable)
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

                // Aquí está el cambio: usamos el operador de llamada segura (?.)
                config.depthMode = when (session?.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
            }

            onSessionUpdated = { _, frame ->
                if (anchorNode == null) {
                    frame.getUpdatedPlanes()
                        .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let { plane ->
                            addAnchorNode(plane.createAnchor(plane.centerPose))
                        }
                }
            }

            onTrackingFailureChanged = { reason ->
                this@ActivityModelos.trackingFailureReason = reason
            }
        }
    }

    private fun addAnchorNode(anchor: Anchor) {
        // Evita añadir más de un ancla
        if (anchorNode != null) return

        // Crea el AnchorNode y lo añade a la escena
        anchorNode = AnchorNode(sceneView.engine, anchor).apply {
            // Permite al usuario mover, rotar y escalar el nodo con gestos
            isEditable = true
            // Carga y construye el modelo 3D de forma asíncrona
            lifecycleScope.launch {
                isLoading = true
                buildModelNode()?.let { modelNode ->
                    addChildNode(modelNode)
                }
                isLoading = false
            }
        }
        sceneView.addChildNode(anchorNode!!)
        Log.d("ActivityModelos", "AnchorNode añadido a la escena.")
        updateInstructions()
    }

    private suspend fun buildModelNode(): ModelNode? {
        // Para la prueba, cargamos directamente carton_car.glb desde res/raw
        val resourceName = "carton_car" // Nombre del archivo sin extensión
        val modelUri = "android.resource://$packageName/raw/$resourceName"

        Log.d("ActivityModelos", "Cargando modelo de prueba desde: $modelUri")

        // Carga la instancia del modelo 3D
        val modelInstance = sceneView.modelLoader.loadModelInstance(modelUri)
        if (modelInstance == null) {
            Log.e("ActivityModelos", "❌ Error al cargar el modelo de prueba desde $modelUri")
            return null
        }

        Log.d("ActivityModelos", "✅ Modelo cargado exitosamente.")
        // Crea y devuelve el ModelNode
        return ModelNode(
            modelInstance = modelInstance,
            // Escala el modelo para que quepa en un cubo de 0.5 metros
            scaleToUnits = 0.5f,
            // Centra el origen en la base del modelo para que se apoye en el suelo
            centerOrigin = Position(y = -0.5f)
        ).apply {
            // Permite que este nodo también sea editable individualmente
            isEditable = true
        }
    }

    // Actualiza el texto de instrucción en la pantalla
    private fun updateInstructions() {
        instructionText.text = trackingFailureReason?.let {
            // Muestra una descripción del error de seguimiento si existe
            it.getDescription(this)
        } ?: if (anchorNode == null) {
            // Pide al usuario que apunte al suelo si aún no se ha colocado el modelo
            getString(R.string.point_your_phone_down)
        } else {
            // Oculta las instrucciones si el modelo ya está en la escena
            null
        }
    }
}