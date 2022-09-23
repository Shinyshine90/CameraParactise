package com.example.core

import android.annotation.SuppressLint
import android.content.Context
import com.example.core.util.CarcorderLog
import android.view.Surface
import android.view.SurfaceView
import android.widget.Toast
import com.example.core.camera.CamerasManager
import com.example.core.camera.session.PreviewCaptureSession
import com.example.core.capture.MediaCaptureManager
import com.example.core.capture.photo.PhotoCapture
import com.example.core.entity.RenderMode
import com.example.core.entity.config.CaptureConfig
import com.example.core.entity.config.RecordConfig
import com.example.core.opengl.CameraCapture
import com.example.core.view.PreviewSurface

@SuppressLint("StaticFieldLeak")
object CarcorderManager {

    private lateinit var context: Context

    private lateinit var cameraCapture: CameraCapture

    private lateinit var camerasManager: CamerasManager

    private lateinit var mediaCaptureManager: MediaCaptureManager

    @Volatile
    private var previewMode = RenderMode.FOUR_SIDE_T

    fun init(context: Context, captureConfig: CaptureConfig) {
        this.context = context
        this.camerasManager = CamerasManager(context)
        this.mediaCaptureManager = MediaCaptureManager(context)
        this.cameraCapture = CameraCapture(context, captureConfig, mediaCaptureManager, this::previewMode)
        CarcorderLog.d("CarcorderManager", "init")
        openCamera()
    }

    fun setRenderMode(mode: RenderMode) {
        previewMode = mode
        CarcorderLog.d("CarcorderManager", "setRenderMode $mode")
    }

    fun startPreview(surfaceView: SurfaceView) {
        cameraCapture.setupPreviewSurface(PreviewSurface(surfaceView))
        CarcorderLog.d("CarcorderManager", "startPreview $surfaceView")
    }

    fun stopPreview() {
        cameraCapture.removePreviewSurface()
        CarcorderLog.d("CarcorderManager", "stopPreview")
    }

    fun capturePhoto(path: String, callback: (Result<String>) -> Unit) {
        mediaCaptureManager.insertPhotoCaptureRequest(PhotoCapture.Request(path, callback))
        CarcorderLog.d("CarcorderManager", "capturePhoto $path")
    }

    fun setRecordConfig(recordConfig: RecordConfig) {
        CarcorderLog.d("CarcorderManager", "setRecordConfig $recordConfig")
    }

    fun startVideoRecord(path: String) {
        mediaCaptureManager.startVideoRecord(path)
        CarcorderLog.d("CarcorderManager", "startVideoRecord $path")
    }

    fun stopVideoRecord() {
        mediaCaptureManager.stopVideoRecord()
        CarcorderLog.d("CarcorderManager", "stopVideoRecord")
    }

    private fun openCamera() {
        val camera = camerasManager.getCameraId(CamerasManager.LensFacing.BACK)
            ?: throw IllegalStateException("get camera direction failed")
        camerasManager.openCamera(camera) { result ->
            if (result.isSuccess) {
                //打开摄像头成功
                val cameraDevice = result.getOrNull() ?: return@openCamera
                cameraCapture.createOESTexture { texture ->
                    val captureSession = PreviewCaptureSession(cameraDevice, Surface(texture))
                    camerasManager.startCapture(captureSession)
                }
            } else {
                //打开摄像头失败、或者摄像头被系统回收
                // TODO:
                CarcorderLog.d("CarcorderManager", "camera error")
                Toast.makeText(context, "Camera Recycled!", Toast.LENGTH_LONG).show()
            }
        }
    }

}