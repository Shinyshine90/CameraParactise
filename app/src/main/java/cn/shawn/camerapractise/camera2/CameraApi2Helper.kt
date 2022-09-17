package cn.shawn.camerapractise.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

private const val TAG = "CameraApi2Helper"

@SuppressLint("MissingPermission")
class CameraApi2Helper(private val context: Context) {

    private val workThread = HandlerThread("Camera2OperateThread").apply { start() }

    private val workHandler by lazy {
        Handler(workThread.looper)
    }

    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private var cameraDevice: CameraDevice? = null

    private var captureSession: CameraCaptureSession? = null

    private var captureRequest: CaptureRequest? = null

    private var captureSurfaceTexture: SurfaceTexture? = null

    fun printCameraInfo() {
        cameraManager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT.toString())
            .printCameraInfo(TAG)
    }

    fun startPreview (
        texture: SurfaceTexture,
        cameraId: String = CameraCharacteristics.LENS_FACING_FRONT.toString()
    ) {
        captureSurfaceTexture = texture
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "onOpened: ")
                cameraDevice = camera
                createCaptureSession(camera, Surface(texture))
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "onDisconnected: ")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "onError: ")
            }
        }, workHandler)
    }

    private fun createCaptureSession(cameraDevice: CameraDevice, surface: Surface) {
        cameraDevice.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    Log.d(TAG, "onConfigured: ")
                    setRepeatingRequest(cameraDevice, session, surface)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.d(TAG, "onConfigureFailed: ")
                }
            },
            workHandler
        )
    }


    fun setRepeatingRequest(
        cameraDevice: CameraDevice,
        session: CameraCaptureSession,
        surface: Surface
    ) {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuilder.addTarget(surface)
        val request = requestBuilder.build()
        this.captureRequest = request
        session.setRepeatingRequest(
            request,
            object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                }
        },
            workHandler
        )
    }

    fun release() {
        captureSession?.stopRepeating()
        captureSession?.abortCaptures()
        captureSession = null
        captureRequest = null
        cameraDevice?.close()
        cameraDevice = null
    }
}