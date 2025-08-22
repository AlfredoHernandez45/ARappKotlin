package com.jose.arappkotlin

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var arCam: ArFragment? = null
    private var clickNo = 0
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modelos)

        if (checkSystemSupport(this) && isARCoreSupportedAndUpToDate()) {
            requestCameraPermission()
        } else {
            finish() // Termina la actividad si el dispositivo no es compatible
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permiso no concedido, solicitarlo.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Permiso ya concedido.
            setupArFragment()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido por el usuario.
                setupArFragment()
            } else {
                // Permiso denegado por el usuario.
                Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setupArFragment() {
        val modelo = intent.getStringExtra("modelo")

        arCam = supportFragmentManager.findFragmentById(R.id.arCameraArea) as ArFragment?

        arCam?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (plane?.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }

            clickNo++
            if (clickNo == 1) {
                val anchor = hitResult.createAnchor()
                loadModel(anchor, modelo)
            }
        }
    }

    private fun loadModel(anchor: Anchor, modelo: String?) {
        if (modelo == null) {
            Toast.makeText(this, "Model name not provided", Toast.LENGTH_SHORT).show()
            return
        }

        ModelRenderable.builder()
            .setSource(this, Uri.parse("models/$modelo"))
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                addModel(anchor, modelRenderable)
            }
            .exceptionally { throwable: Throwable ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Something is not right: ${throwable.message}").show()
                null
            }
    }

    private fun addModel(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arCam!!.arSceneView.scene)

        val model = TransformableNode(arCam!!.transformationSystem)
        model.setParent(anchorNode)
        model.renderable = modelRenderable
        model.select()
    }

    private fun isARCoreSupportedAndUpToDate(): Boolean {
        return when (ArCoreApk.getInstance().checkAvailability(this)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                try {
                    when (ArCoreApk.getInstance().requestInstall(this, true)) {
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            Log.i(HeadlessEngineWrapper.TAG, "ARCore installation requested.")
                            false
                        }
                        ArCoreApk.InstallStatus.INSTALLED -> true
                    }
                } catch (e: UnavailableException) {
                    Log.e(HeadlessEngineWrapper.TAG, "ARCore not installed", e)
                    false
                }
            }
            else -> false
        }
    }

    private fun checkSystemSupport(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_LONG).show()
            return false
        }
        val openGlVersion = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
        if (openGlVersion.toDouble() < 3.0) {
            Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
