package com.example.opengl.base

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class BaseGLSurfaceView constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    init {
        setEGLContextClientVersion(2)
    }
}