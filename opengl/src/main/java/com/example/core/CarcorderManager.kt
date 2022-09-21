package com.example.core

import android.annotation.SuppressLint
import android.content.Context
import android.view.Surface
import android.view.SurfaceView
import com.example.core.camera.CamerasManager
import com.example.core.camera.session.PreviewCaptureSession
import com.example.core.entity.RenderMode
import com.example.core.entity.config.CaptureConfig
import com.example.core.opengl.CameraCapture
import com.example.core.view.PreviewSurface

@SuppressLint("StaticFieldLeak")
object CarcorderManager {

    private lateinit var cameraCapture: CameraCapture

    private lateinit var camerasManager: CamerasManager

    private var renderMode = RenderMode.FOUR_SIDE_T

    fun init(context: Context, captureConfig: CaptureConfig) {
        cameraCapture = CameraCapture(context, captureConfig, this::renderMode)
        camerasManager = CamerasManager(context)
        openCamera()
    }

    fun startPreview(surfaceView: SurfaceView) {
        cameraCapture.setupPreviewSurface(PreviewSurface(surfaceView))
    }

    fun stopPreview() {
        cameraCapture.removePreviewSurface()
    }

    fun setRenderMode(mode: RenderMode) {
        renderMode = mode
    }

    private fun openCamera() {
        val camera = camerasManager.getCameraId(CamerasManager.LensFacing.BACK)
            ?: throw IllegalStateException("get camera direction failed")
        camerasManager.openCamera(camera) {
            val cameraDevice = it.getOrThrow()
            cameraCapture.createOESTexture { texture ->
                val captureSession = PreviewCaptureSession(cameraDevice, Surface(texture))
                camerasManager.startCapture(captureSession)
            }
        }
    }

}