package com.example.core.util.ext

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.toFloatBuffer(): FloatBuffer =
    this.run {
        ByteBuffer.allocateDirect(this.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(this).apply { position(0) }
    }

