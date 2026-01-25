package io.github.sceneview.sample.armodelviewer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Monument(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int? = null,
    val modelResId: Int? = null,
    val imageUrl: String? = null,
    val modelUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable
