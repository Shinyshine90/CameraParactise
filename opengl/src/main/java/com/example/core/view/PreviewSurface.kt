package com.example.core.view

import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

class PreviewSurface(
    view: SurfaceView
) {

    var _surfaceWidth: Int = -1
        private set

    var _surfaceHeight: Int = -1
        private set

    private var surfaceView: SurfaceView? = null

    private var onSurfaceCreated: ((Surface) -> Unit)? = null

    private var surfaceCallback = object : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            onSurfaceCreated?.invoke(holder.surface)
            onSurfaceCreated = null
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            _surfaceWidth = width
            _surfaceHeight = height
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }
    }

    fun setOnCreateCallback(onSurfaceCreated: (Surface) -> Unit) {
        this.onSurfaceCreated = onSurfaceCreated
        this.surfaceView?.holder?.addCallback(surfaceCallback)
    }

    fun release() {
        this.surfaceView?.holder?.removeCallback(surfaceCallback)
        this.onSurfaceCreated = null
    }

    init {
        this.surfaceView = view
    }
}