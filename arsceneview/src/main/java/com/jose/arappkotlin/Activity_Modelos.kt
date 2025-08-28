package com.jose.arappkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import io.github.sceneview.model.ModelInstance // New import
import io.github.sceneview.ar.R // Explicit import for R class
import android.util.Log // Import for Log.e
import io.github.sceneview.math.Position

class Activity_Modelos : AppCompatActivity() {

    private lateinit var sceneView: SceneView
    private var modelNode: ModelNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelos)

        sceneView = findViewById(R.id.scene_view)

        val modelo = intent.getStringExtra("modelo")

        if (modelo != null) {
            // Get the resource ID for the GLB model
            val modelResourceId = resources.getIdentifier(modelo.substringBefore("."), "raw", packageName)
            val modelUri = "android.resource://${packageName}/${modelResourceId}"

            // Load the model asynchronously
            sceneView.modelLoader.loadModelAsync(modelUri) { model ->
                if (model != null) {
                    // Create a ModelNode with the loaded model
                    val modelInstance = sceneView.modelLoader.createInstance(model)
                    if (modelInstance != null) {
                        modelNode = ModelNode(modelInstance = modelInstance).apply {
                            // You can apply transformations or other properties here if needed
                        }
                        sceneView.addChildNode(modelNode!!)
                    } else {
                        Log.e("Activity_Modelos", "Failed to create model instance for: $modelo")
                    }
                } else {
                    Log.e("Activity_Modelos", "Failed to load model: $modelo")
                }
            }
        }
    }
}