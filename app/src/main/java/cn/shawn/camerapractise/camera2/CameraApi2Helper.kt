package cn.shawn.camerapractise.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

private const val TAG  = "CameraApi2Helper"

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


    fun openCamera(cameraId: String = CameraCharacteristics.LENS_FACING_FRONT.toString()) {
        cameraManager.openCamera(cameraId, object: CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "onOpened: ")
                cameraDevice = camera
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "onDisconnected: ")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "onError: ")
            }
        }, workHandler)
    }

    fun setupSurfaceTexture(texture: SurfaceTexture) {
        cameraDevice.execute { cameraDevice ->
            val surface = Surface(texture)
            createCaptureSession(surface)
            createCaptureRequest(surface)
        }
    }

    private fun createCaptureSession(surface: Surface) {
        cameraDevice.execute { cameraDevice ->
            cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }
            }, workHandler)
        }
    }

    private fun createCaptureRequest(surface: Surface) {
        cameraDevice.execute { cameraDevice ->
            val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            requestBuilder.addTarget(surface)
            requestBuilder.build()
        }
    }

    fun startPreview() {
        val session = captureSession ?: return
        val request = captureRequest ?: return
        session.setRepeatingRequest(request, object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureStarted(session: CameraCaptureSession,
                                          request: CaptureRequest, timestamp: Long, frameNumber: Long) {
                super.onCaptureStarted(session, request, timestamp, frameNumber)

            }

            override fun onCaptureCompleted(session: CameraCaptureSession,
                                            request: CaptureRequest, result: TotalCaptureResult) {
                super.onCaptureCompleted(session, request, result)

            }

            override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
                super.onCaptureFailed(session, request, failure)
            }
        }, workHandler)
    }

    fun stopPreview() {

    }

    fun release() {

        cameraDevice = null
    }

    private fun CameraDevice?.execute(task: (CameraDevice) -> Unit) {
        this ?: return
        task(this)
    }
}