package com.smartlink.foundation.camera.session

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface

abstract class BaseCaptureSession(
    val camera: CameraDevice,
    val surface: Surface,
    protected val callbackHandler: Handler = Handler(Looper.getMainLooper())
) {

    protected var captureSession: CameraCaptureSession? = null

    protected abstract fun createCaptureSession(callback: (Result<CameraCaptureSession>) -> Unit)

    protected abstract fun createRepeatingRequest(session: CameraCaptureSession): CaptureRequest

    abstract fun startCapture(callback: (Result<CameraCaptureSession>) -> Unit)

    fun stopCaptureSession() {
        try {
            captureSession?.stopRepeating()
            captureSession?.close()
        } catch (e: IllegalStateException) {
            Log.e("CaptureSession", "stopCaptureSession failed", e)
        }
    }
}