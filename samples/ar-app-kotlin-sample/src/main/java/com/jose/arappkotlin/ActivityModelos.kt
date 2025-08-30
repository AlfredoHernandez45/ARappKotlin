package com.jose.arappkotlin

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode

// Activity responsible for displaying 3D models in an Augmented Reality (AR) scene.
class ActivityModelos : AppCompatActivity() {

    // ARSceneView is the main view for displaying AR content.
    private lateinit var sceneView: ARSceneView
    // modelNode holds the 3D model to be displayed in the AR scene.
    private var modelNode: ModelNode? = null
    // modelInstance is the instance of the loaded 3D model.
    private var modelInstance: ModelInstance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for this activity.
        setContentView(R.layout.activity_modelos)

        // Initialize the ARSceneView from the layout.
        sceneView = findViewById(R.id.scene_view)
        // Enable plane rendering to visualize detected planes in the AR scene.
        sceneView.planeRenderer.isEnabled = true

        // Retrieve the model resource ID passed from the previous activity.
        val modelResourceId = intent.getIntExtra("modelResourceId", 0)
        // Log the received model resource ID for debugging.
        Log.d("ActivityModelos", "Received modelResourceId: $modelResourceId")

        // Check if a valid model resource ID was provided.
        if (modelResourceId != 0) {
            // Construct the URI for the 3D model resource.
            // Models are expected to be in the 'res/raw' folder.
            val modelUri = "android.resource://$packageName/$modelResourceId"
            // Log the constructed model URI for debugging.
            Log.d("ActivityModelos", "Constructed model URI: $modelUri")

            // Asynchronously load the 3D model using SceneView's modelLoader.
            sceneView.modelLoader.loadModelAsync(modelUri) { model ->
                // Check if the model was loaded successfully.
                if (model != null) {
                    // Create an instance of the loaded model.
                    modelInstance = sceneView.modelLoader.createInstance(model)
                    // Log the success or failure of model instance creation.
                    if (modelInstance != null) {
                        Log.d("ActivityModelos", "Model instance created successfully for resource ID: $modelResourceId")
                    } else {
                        Log.e("ActivityModelos", "Failed to create model instance for resource ID: $modelResourceId")
                    }
                } else {
                    // Log an error if the model failed to load.
                    Log.e("ActivityModelos", "Failed to load model with resource ID: $modelResourceId")
                }
            }
        } else {
            // Log an error if no valid model resource ID was provided.
            Log.e("ActivityModelos", "No model resource ID provided in intent.")
        }

        // Set an OnTouchEvent listener for the ARSceneView to handle user interactions.
        sceneView.onTouchEvent = { motionEvent, hitResult ->
            // Process only when a touch down event occurs.
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Perform an AR hit test at the touch coordinates to find a real-world surface.
                val arHitResult = sceneView.hitTestAR(motionEvent.x, motionEvent.y)
                // Check if a valid AR hit result was obtained.
                if (arHitResult != null) {
                    // If a model instance exists, proceed with placing it.
                    modelInstance?.let { instance ->
                        // If a model is already placed in the scene, remove it first.
                        modelNode?.let {
                            it.parent?.removeChildNode(it) // Remove from its parent node.
                            it.destroy() // Destroy the node to release resources.
                        }

                        // Create a new AnchorNode at the hit test location.
                        // An AnchorNode is used to place virtual content in the real world.
                        val anchorNode = AnchorNode(sceneView.engine, arHitResult.createAnchor())
                        // Add the AnchorNode to the AR scene.
                        sceneView.addChildNode(anchorNode)

                        // Create a new ModelNode with the loaded model instance.
                        modelNode = ModelNode(modelInstance = instance)
                        // Add the ModelNode as a child of the AnchorNode.
                        // This links the 3D model to the real-world anchor.
                        anchorNode.addChildNode(modelNode!!)
                        // Log that the model has been placed.
                        Log.d("ActivityModelos", "Model placed at AR hit result.")
                    }
                }
            }
            // Consume the event to prevent it from being processed by other listeners.
            true
        }
    }
}