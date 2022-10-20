package com.smartlink.foundation.camera.session

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.os.Handler
import android.os.Looper
import android.view.Surface

class PreviewCaptureSession(
    camera: CameraDevice,
    surface: Surface,
    callbackHandler: Handler = Handler(Looper.getMainLooper())
) : BaseCaptureSession(camera, surface, callbackHandler) {

    @Suppress("DEPRECATION")
    override fun createCaptureSession(callback: (Result<CameraCaptureSession>) -> Unit) {
        camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                callback(Result.success(session))
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                callback(Result.failure(RuntimeException("create session failed")))
            }
        }, callbackHandler)
    }

    override fun createRepeatingRequest(session: CameraCaptureSession): CaptureRequest {
        return camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).run {
            this.addTarget(surface)
            build()
        }
    }

    override fun startCapture(callback: (Result<CameraCaptureSession>) -> Unit) {
        createCaptureSession { result ->
            if (result.isSuccess) {
                result.getOrNull()?.apply {
                    setRepeatingRequest(createRepeatingRequest(this), null, callbackHandler)
                    callback(Result.success(this))
                } ?: callback(Result.failure(RuntimeException("create session empty")))
            } else {
                callback(Result.failure(RuntimeException("create session failed")))
            }
        }
    }


}