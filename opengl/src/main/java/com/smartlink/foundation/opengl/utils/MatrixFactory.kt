package com.smartlink.foundation.opengl.utils

import kotlin.math.cos
import kotlin.math.sin

/** Simple 2D operate matrix factory */
object MatrixFactory {

    fun createNormaMatrix() =
        floatArrayOf(
            1f, 0.0f, 0.0f, 0.0f,
            0.0f, 1f, 0.0f, 0.0f,
            0.0f, 0.0f, 1f, 0.0f,
            0.0f, 0.0f, 0.0f, 1f
        )

    fun createScaleXYMatrix(scaleX: Float, scaleY: Float): FloatArray =
        floatArrayOf(
            scaleX, 0.0f, 0.0f, 0.0f,
            0.0f, scaleY, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )

    fun createTranslateXYMatrix(offsetX: Float, offsetY: Float): FloatArray =
        floatArrayOf(
            1f, 0.0f, 0.0f, offsetX,
            0.0f, 1f, 0.0f, offsetY,
            0.0f, 0.0f, 1f, 0.0f,
            0.0f, 0.0f, 0.0f, 1f
        )

    fun createRotateXYMatrix(degrees: Float): FloatArray =
        floatArrayOf(
            cos(degrees), sin(degrees), 0f, 0f,
            -sin(degrees), cos(degrees), 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
}