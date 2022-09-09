package com.example.opengl.util.ext

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

fun scaleXYMatrix(scaleX: Float, scaleY: Float): FloatArray =
    floatArrayOf(
        scaleX, 0.0f, 0.0f, 0.0f,
        0.0f, scaleY, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    )

fun translateXYMatrix(offsetX: Float, offsetY: Float): FloatArray =
    floatArrayOf(
        1f, 0.0f, 0.0f, 0.0f,
        0.0f, 1f, 0.0f, 0.0f,
        0.0f, 0.0f, 1f, 0.0f,
        offsetX, offsetY, 1.0f, 1f
    )

fun rotateXYMatrix(degrees: Float): FloatArray =
    floatArrayOf(
        cos(degrees), sin(degrees), 0f, 0f,
        -sin(degrees), cos(degrees), 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

fun FloatArray.toFloatBuffer(): FloatBuffer =
    this.run {
        ByteBuffer.allocateDirect(this.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(this).apply { position(0) }
    }

