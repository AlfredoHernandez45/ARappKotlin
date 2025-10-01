// Define el paquete al que pertenece esta clase.
package com.jose.arappkotlin

// Importa las clases necesarias de Android y otras bibliotecas.
import android.os.Bundle // Utilizado para pasar datos entre actividades y guardar el estado de la aplicación.
import android.util.Log // Herramienta para enviar mensajes de registro al sistema (logcat).
import android.view.MotionEvent // Representa un evento de movimiento, como un toque en la pantalla.
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades que usan la barra de aplicaciones de compatibilidad.
import io.github.sceneview.ar.ARSceneView // La vista principal que renderiza la escena de Realidad Aumentada.
import io.github.sceneview.ar.node.AnchorNode // Un nodo que se ancla a una superficie del mundo real detectada.
import io.github.sceneview.model.ModelInstance // Una instancia de un modelo 3D que se puede renderizar.
import io.github.sceneview.node.ModelNode // Un nodo en el grafo de la escena que contiene y muestra un modelo 3D.
import android.net.Uri


// Actividad responsable de mostrar modelos 3D en una escena de Realidad Aumentada (AR).
class ActivityModelos : AppCompatActivity() {

    // ARSceneView es la vista principal para mostrar contenido AR.
    private lateinit var sceneView: ARSceneView
    // modelNode contiene el modelo 3D que se va a mostrar en la escena AR.
    private var modelNode: ModelNode? = null
    // modelInstance es la instancia del modelo 3D cargado.
    private var modelInstance: ModelInstance? = null

    // Este método se llama cuando la actividad se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación de la clase base.
        super.onCreate(savedInstanceState)
        // Establece el archivo de diseño (layout) para esta actividad.
        setContentView(R.layout.activity_modelos)

        // Inicializa la ARSceneView desde el layout, encontrándola por su ID.
        sceneView = findViewById(R.id.arCameraArea)
        // Habilita el renderizador de planos para visualizar las superficies detectadas en la escena AR.
        sceneView.planeRenderer.isEnabled = true

        // Recupera el ID del recurso del modelo pasado desde la actividad anterior.
        val modelResourceId = intent.getIntExtra("modelResourceId", 0)
        // Registra el ID del recurso del modelo recibido para fines de depuración.
        Log.d("ActivityModelos", "ID de recurso del modelo recibido: $modelResourceId")

        // Comprueba si se proporcionó un ID de recurso de modelo válido.
        //if (modelResourceId != 0) {
            // Construye el URI para el recurso del modelo 3D.
            // Se espera que los modelos estén en la carpeta 'res/raw'.
            //val modelUri = "android.resource://$packageName/$modelResourceId"
            val modelFileName = intent.getStringExtra("modelFileName")

            if (modelFileName != null) {
                //val modelUri = "file:///android_asset/models/$modelFileName"
                //val modelUri = Uri.parse("android.resource://$packageName/raw/${modelFileName.substringBefore(".")}")
                val modelUri = "android.resource://$packageName/raw/${modelFileName.substringBefore(".")}"

                Log.d("ActivityModelos", "Loading model from assets: $modelUri")

                sceneView.modelLoader.loadModelAsync(modelUri) { model ->
                    if (model != null) {
                        modelInstance = sceneView.modelLoader.createInstance(model)
                        Log.d("ActivityModelos", "✅ Modelo cargado desde assets")
                    } else {
                        Log.e("ActivityModelos", "❌ Error al cargar modelo desde assets")
                    }
                }
            } else {
                Log.e("ActivityModelos", "No se recibió nombre de archivo del modelo.")
            }




            // Registra el URI del modelo construido para fines de depuración.
            //Log.d("ActivityModelos", "URI del modelo construido: $modelUri")

            // Carga de forma asíncrona el modelo 3D utilizando el modelLoader de SceneView.
//            sceneView.modelLoader.loadModelAsync(modelUri) { model ->
//                // Comprueba si el modelo se cargó correctamente.
//                if (model != null) {
//                    // Crea una instancia del modelo cargado.
//                    modelInstance = sceneView.modelLoader.createInstance(model)
//                    // Registra si la creación de la instancia del modelo fue exitosa o no.
//                    if (modelInstance != null) {
//                        Log.d("ActivityModelos", "Instancia del modelo creada con éxito para el ID de recurso: $modelResourceId")
//                    } else {
//                        Log.e("ActivityModelos", "Error al crear la instancia del modelo para el ID de recurso: $modelResourceId")
//                    }
//                } else {
//                    // Registra un error si el modelo no se pudo cargar.
//                    Log.e("ActivityModelos", "Error al cargar el modelo con ID de recurso: $modelResourceId")
//                }
//            }
        //} else {
            // Registra un error si no se proporcionó un ID de recurso de modelo válido.
            // Log.e("ActivityModelos", "No se proporcionó ningún ID de recurso de modelo en el intent.")
        //}

        // Establece un listener OnTouchEvent para que la ARSceneView maneje las interacciones del usuario.
        sceneView.onTouchEvent = { motionEvent, hitResult ->
            // Procesa solo cuando ocurre un evento de tocar la pantalla (acción de bajar el dedo).
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Realiza una prueba de impacto AR en las coordenadas del toque para encontrar una superficie del mundo real.
                val arHitResult = sceneView.hitTestAR(motionEvent.x, motionEvent.y)
                // Comprueba si se obtuvo un resultado de impacto AR válido.
                if (arHitResult != null) {
                    // Si existe una instancia de modelo, procede a colocarla.
                    modelInstance?.let { instance ->
                        // Si ya hay un modelo colocado en la escena, elimínalo primero.
                        modelNode?.let {
                            it.parent?.removeChildNode(it) // Lo elimina de su nodo padre.
                            it.destroy() // Destruye el nodo para liberar recursos.
                        }

                        // Crea un nuevo AnchorNode en la ubicación de la prueba de impacto.
                        // Un AnchorNode se utiliza para anclar contenido virtual al mundo real.
                        val anchorNode = AnchorNode(sceneView.engine, arHitResult.createAnchor())
                        // Añade el AnchorNode a la escena AR.
                        sceneView.addChildNode(anchorNode)

                        // Crea un nuevo ModelNode con la instancia del modelo cargado.
                        modelNode = ModelNode(modelInstance = instance)
                        // Añade el ModelNode como hijo del AnchorNode.
                        // Esto vincula el modelo 3D al ancla del mundo real.
                        anchorNode.addChildNode(modelNode!!)
                        // Registra que el modelo ha sido colocado.
                        Log.d("ActivityModelos", "Modelo colocado en el resultado del impacto AR.")
                    }
                }
            }
            // Consume el evento para evitar que sea procesado por otros listeners.
            true
        }
    }
}
