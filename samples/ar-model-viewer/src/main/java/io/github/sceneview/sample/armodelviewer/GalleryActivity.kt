package io.github.sceneview.sample.armodelviewer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import io.github.sceneview.sample.setFullScreen
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity(R.layout.activity_gallery) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(
            findViewById(R.id.recyclerView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val defaultMonuments = listOf(
            Monument(
                1,
                "Manatí",
                "Esta atracción turística se encuentra frente a la bella Bahía de Chetumal, entre el Centro Internacional de Convenciones y la Universidad de Quintana Roo (UQROO), fue inaugurada en 1996 con el objetivo de darle realce al Boulevard Bahía y conmemorar a los Manatíes, los amigables mamíferos acuáticos que abundan en la Bahía, la cual fue decretada como santuario del Manatí.",
                R.drawable.santuario_manati,
                R.raw.manati_4,
                null, null,
                18.5198278, -88.2697338
            ),
            Monument(
                2,
                "Lázaro Cárdenas",
                "Esta obra artística se encuentra en la glorieta del cruce del boulevard Bahía y la calzada Veracruz en la ciudad de Chetumal, fue erigida en honor a Lázaro Cárdenas, presidente de México en los años 1934-1940, quien se caracterizó por realizar importantes acciones de gobierno como la reforma agraria, la creación de los ejidos, así como la reincorporación de Quintana Roo como entidad federativa el 16 de enero de 1935, hecho por el cual se convirtió en personaje fundamental en la historia de Quintana Roo.",
                R.drawable.lazaro_cardenaz,
                R.raw.lazaro_cardenas_v4,
                null, null,
                18.493757548441085, -88.29140410180933
            ),
            Monument(
                3,
                "Monumento a la Mujer",
                "Monumento inaugurado durante el gobierno de Aaron Merino Fernández (1958 - 1964), obra de artista H. Juárez. La estatura se encuentra esculpida sobre un cerro de prismas y caracteriza a la mujer chetumaleña aludiendo el traje típico que representa al estado de Quintana Roo, con imágenes representativas de la cultura Maya. Representando a la mujer quintanarroense, Ramon Valdiosera Berman fue quien diseño el traje inspirado en las llamadas \"Mujeres de la isla Jaina\" y creado por encargo de Carmen Ochoa de Merino.",
                R.drawable.monumento_a_mujer_la_chetumalena,
                R.raw.leona_vicario_v4,
                null, null,
                18.5027867507285, -88.29536073489334
            )
        )

        lifecycleScope.launch {
            val apiMonuments = SupabaseApi.fetchMonuments()
            val allMonuments = defaultMonuments + apiMonuments
            
            recyclerView.adapter = MonumentAdapter(allMonuments) { monument ->
                val intent = Intent(this@GalleryActivity, DetailsActivity::class.java).apply {
                    putExtra("EXTRA_MONUMENT", monument)
                }
                startActivity(intent)
            }
        }
    }
}
