package cn.shawn.camerapractise.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Log
import android.util.Size
import android.view.Surface
import cn.shawn.camerapractise.util.ImageUtils
import java.io.File

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

    private val previewSize = Size(1280, 720)

    private val imageReader by lazy {
        ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.YUV_420_888, 1)
            .apply {
                setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener {
                    override fun onImageAvailable(reader: ImageReader?) {
                        reader ?: return
                        reader.acquireNextImage().apply {
                            val yLength= this.planes[0].buffer.remaining()
                            val uLength= this.planes[1].buffer.remaining()
                            val vLength= this.planes[2].buffer.remaining()
                            Log.d(TAG, "onImageAvailable: thread=${Thread.currentThread().name}, " +
                                    "width=${this.width}, height=${this.height}, yLength=$yLength, uLength=$uLength, vLength=$vLength")
                            this.close()
                        }
                    }
                }, workHandler)
            }
    }

    fun printCameraInfo() {
        cameraManager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT.toString()).printCameraInfo(TAG)
    }

    fun startCapture(cameraId: String = CameraCharacteristics.LENS_FACING_FRONT.toString(), surfaceTexture: SurfaceTexture) {
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "onOpened: ")
                cameraDevice = camera
                createCaptureSession(camera, Surface(surfaceTexture))
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


    fun setRepeatingRequest(cameraDevice: CameraDevice, session: CameraCaptureSession, surface: Surface) {
        val requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        requestBuilder.addTarget(surface)
        val request = requestBuilder.build()
        this.captureRequest = request
        session.setRepeatingRequest(request, object : CameraCaptureSession.CaptureCallback() {}, workHandler)
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