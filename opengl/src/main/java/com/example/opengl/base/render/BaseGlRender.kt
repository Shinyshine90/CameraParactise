package com.example.opengl.base.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class BaseGlRender(protected val context: Context): GLSurfaceView.Renderer {

    var _surfaceWidth: Int = 0
        private set

    var _surfaceHeight: Int = 0
        private set

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        _surfaceWidth = width
        _surfaceHeight = height
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glViewport(0, 0, _surfaceWidth, _surfaceHeight)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

}