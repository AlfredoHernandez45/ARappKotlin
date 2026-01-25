package io.github.sceneview.sample.armodelviewer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import android.view.MotionEvent
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.gesture.GestureDetector
import io.github.sceneview.node.Node
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.sample.doOnApplyWindowInsets
import io.github.sceneview.sample.setFullScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    lateinit var sceneView: ARSceneView
    lateinit var loadingView: View
    lateinit var instructionText: TextView

    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
        }

    var modelNode: ModelNode? = null
    var modelUrl: String? = null
    var modelResId: Int = 0

    var anchorNode: AnchorNode? = null
        set(value) {
            if (field != value) {
                field = value
                updateInstructions()
            }
        }

    var anchorNodeView: View? = null

    var trackingFailureReason: TrackingFailureReason? = null
        set(value) {
            if (field != value) {
                field = value
                updateInstructions()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
            }
            title = ""
        })
        instructionText = findViewById(R.id.instructionText)
        loadingView = findViewById(R.id.loadingView)
        sceneView = findViewById<ARSceneView>(R.id.sceneView).apply {
            lifecycle = this@MainActivity.lifecycle
            planeRenderer.isEnabled = true
            configureSession { session, config ->
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }
            onSessionUpdated = { _, frame ->
                if (anchorNode == null) {
                    val camera = frame.camera
                    if (camera.trackingState == TrackingState.TRACKING) {
                        val plane = frame.getUpdatedPlanes()
                            .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        if (plane != null) {
                            Log.d("SceneView", "Floor detected! Placing model...")
                            addAnchorNode(plane.createAnchor(plane.centerPose))
                        }
                    }

                    // Throttled log to see state every 2 seconds
                    if (System.currentTimeMillis() % 2000 < 100) {
                        Log.d("SceneView", "TrackingState: ${camera.trackingState}, Planes: ${frame.getUpdatedPlanes().size}")
                    }
                }
            }
            onTrackingFailureChanged = { reason ->
                Log.d("SceneView", "Tracking failure: $reason")
                this@MainActivity.trackingFailureReason = reason
            }
            onGestureListener = object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent, node: Node?) {
                    if (anchorNode == null && node == null) {
                        hitTestAR(e.x, e.y)?.let { hitResult ->
                            Log.d("SceneView", "Manual tap detected! Placing model...")
                            addAnchorNode(hitResult.createAnchor())
                        }
                    }
                }
            }
        }

        modelResId = intent.getIntExtra("EXTRA_MODEL_RES_ID", 0)
        modelUrl = intent.getStringExtra("EXTRA_MODEL_URL")

        // Pre-load the model
        lifecycleScope.launch {
            isLoading = true
            modelNode = buildModelNode()
            isLoading = false
        }
//        sceneView.viewNodeWindowManager = ViewAttachmentManager(context, this).apply { onResume() }
    }

    fun updateInstructions() {
        instructionText.text = trackingFailureReason?.let {
            it.getDescription(this)
        } ?: if (anchorNode == null) {
            "Mueve la cámara lento o toca el piso si no aparece el modelo"
        } else {
            null
        }
    }

    fun addAnchorNode(anchor: Anchor) {
        sceneView.addChildNode(
            AnchorNode(sceneView.engine, anchor)
                .apply {
                    isEditable = true
                    modelNode?.let {
                        Log.d("SceneView", "Attaching pre-loaded model to anchor")
                        addChildNode(it)
                    } ?: run {
                        Log.w("SceneView", "Model not ready yet, waiting for it...")
                        lifecycleScope.launch {
                            isLoading = true
                            // If buildModelNode is already running, we might be reloading here.
                            // To be safe, we call buildModelNode again but it will be on IO and won't block.
                            modelNode = buildModelNode()
                            modelNode?.let { addChildNode(it) }
                            isLoading = false
                        }
                    }
                    anchorNode = this
                }
        )
    }

    suspend fun buildModelNode(): ModelNode? {
        Log.d("SceneView", "Loading model: resId=$modelResId, url=$modelUrl")
        val modelInstance = if (modelResId != 0) {
            sceneView.modelLoader.createModelInstance(modelResId)
        } else if (!modelUrl.isNullOrEmpty()) {
            sceneView.modelLoader.loadModelInstance(modelUrl!!)
        } else {
            // Fallback to DamagedHelmet if nothing else is provided
            sceneView.modelLoader.loadModelInstance("https://sceneview.github.io/assets/models/DamagedHelmet.glb")
        }

        return modelInstance?.let { instance ->
            Log.d("SceneView", "Model loaded successfully")
            ModelNode(
                modelInstance = instance,
                // Scale to fit in a 0.5 meters cube
                scaleToUnits = 0.5f,
                // Bottom origin instead of center so the model base is on floor
                centerOrigin = Position(y = -0.5f)
            ).apply {
                isEditable = true
            }
        } ?: run {
            Log.e("SceneView", "Failed to load model instance")
            null
        }
    }

//    suspend fun buildViewNode(): ViewNode? {
//        return withContext(Dispatchers.Main) {
//            val engine = sceneView.engine
//            val materialLoader = sceneView.materialLoader
//            val windowManager = sceneView.viewNodeWindowManager ?: return@withContext null
//            val view = LayoutInflater.from(materialLoader.context).inflate(R.layout.view_node_label, null, false)
//            val ViewAttachmentManager(context, this).apply { onResume() }
//            val viewNode = ViewNode(engine, windowManager, materialLoader, view, true, true)
//            viewNode.position = Position(0f, -0.2f, 0f)
//            anchorNodeView = view
//            viewNode
//        }
//    }

}