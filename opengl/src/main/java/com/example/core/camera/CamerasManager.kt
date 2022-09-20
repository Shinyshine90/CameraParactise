package com.example.core.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.core.camera.ext.printCameraInfo
import com.example.core.camera.session.BaseCaptureSession
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet

private const val TAG = "CamerasManager"

@SuppressLint("MissingPermission")
class CamerasManager(
    private val context: Context,
    private val callbackHandler: Handler = Handler(Looper.getMainLooper())
) {

    private val cameras by lazy {
        ConcurrentHashMap<String, CameraDevice>()
    }

    private val sessions: MutableSet<in BaseCaptureSession> by lazy {
        Collections.synchronizedSet(HashSet())
    }

    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun getCameraId(lensFacing: LensFacing): String? {
        return cameraManager.cameraIdList.firstOrNull { id ->
            lensFacing.facing == cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.LENS_FACING)
        }
    }

    fun printCameraInfo(cameraId: String, tag: String) {
        try {
            cameraManager.getCameraCharacteristics(cameraId).printCameraInfo(tag)
        } catch (e: CameraAccessException) {
            Log.e(tag, "printCameraInfo: error")
        }
    }

    fun openCamera(cameraId: String, callback: (Result<CameraDevice>) -> Unit) {
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                cameras[camera.id] = camera
                callback(Result.success(camera))
            }

            override fun onDisconnected(camera: CameraDevice) {
                onCameraDisconnect(camera)
            }

            override fun onError(camera: CameraDevice, error: Int) {
                callback(Result.failure(RuntimeException("camera $cameraId open failed, cause $error")))
            }
        }, callbackHandler)
    }

    fun closeCamera(cameraId: String) {
        cameras[cameraId]?.apply {
            closeAllSession(this)
            this.close()
        }
        cameras.remove(cameraId)
    }

    fun <T: BaseCaptureSession> startCapture(session: T) {
        if (sessions.contains(session)) return
        session.startCapture { result ->
            Log.d(TAG, "startCapture result ${result.isSuccess} ${result.getOrNull()}")
            result.getOrNull()?.apply {
                sessions.add(session)
            }
        }
    }

    fun <T: BaseCaptureSession> stopCapture(session: T) {
        if (!sessions.contains(session)) return
        session.stopCaptureSession()
        sessions.remove(session)
    }

    private fun onCameraDisconnect(camera: CameraDevice) {
        closeAllSession(camera)
        cameras.remove(camera.id)
    }

    private fun closeAllSession(camera: CameraDevice) {
        sessions.filter {
            it is BaseCaptureSession && it.camera == camera
        }.forEach {
            stopCapture(it as BaseCaptureSession)
        }
    }

    enum class LensFacing(val facing: Int) {
        FRONT(CameraCharacteristics.LENS_FACING_FRONT),
        BACK(CameraCharacteristics.LENS_FACING_BACK)
    }
}