package com.jose.arappkotlin

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class Activity_Modelos : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelos)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        val modelo = intent.getStringExtra("modelo")

        if (modelo != null) {
            arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
                loadModelAndPlace(hitResult.createAnchor(), modelo)
            }
        }
    }

    private fun loadModelAndPlace(anchor: Anchor, modelo: String) {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("models/$modelo"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { modelRenderable ->
                placeModel(anchor, modelRenderable)
            }
            .exceptionally { throwable ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Something is not right: ${throwable.message}").show()
                null
            }
    }

    private fun placeModel(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)
        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }
}
