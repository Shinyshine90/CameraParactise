package com.smartlink.foundation.opengl.entity

sealed class SurfaceSize(var width: Int, var height: Int) {

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    class DisplaySurfaceSize(width: Int = 0, height: Int = 0): SurfaceSize(width, height)

    class FrameBufferSize(width: Int = 0, height: Int = 0): SurfaceSize(width, height)
}