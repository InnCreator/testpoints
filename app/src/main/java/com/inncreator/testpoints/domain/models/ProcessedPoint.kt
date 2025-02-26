package com.inncreator.testpoints.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessedPoint(
    val counter: Int,
    val x: Float,
    val y: Float
) : Parcelable