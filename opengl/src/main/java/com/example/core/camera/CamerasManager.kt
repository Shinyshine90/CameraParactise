package com.example.core.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import com.example.core.util.CarcorderLog
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
            CarcorderLog.e(tag, "printCameraInfo: error")
        }
    }

    fun openCamera(cameraId: String, callback: (Result<CameraDevice>) -> Unit) {
        CarcorderLog.d(TAG, "openCamera $cameraId")
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                cameras[camera.id] = camera
                callback(Result.success(camera))
                CarcorderLog.d(TAG, "onOpened: $cameraId")
            }

            override fun onDisconnected(camera: CameraDevice) {
                onCameraDisconnect(camera)
                CarcorderLog.d(TAG, "onDisconnected: $cameraId")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                callback(Result.failure(RuntimeException("camera $cameraId open failed, cause $error")))
                CarcorderLog.d(TAG, "onError: $cameraId error $error")
            }
        }, callbackHandler)
    }

    fun closeCamera(cameraId: String) {
        CarcorderLog.d(TAG, "close camera $cameraId")
        cameras[cameraId]?.apply {
            closeAllSession(this)
            this.close()
        }
        cameras.remove(cameraId)
    }

    fun <T: BaseCaptureSession> startCapture(session: T) {
        CarcorderLog.d(TAG, "startCapture")
        if (sessions.contains(session)) return
        session.startCapture { result ->
            CarcorderLog.d(TAG, "startCapture result ${result.isSuccess} ${result.getOrNull()}")
            result.getOrNull()?.apply {
                sessions.add(session)
            }
        }
    }

    fun <T: BaseCaptureSession> stopCapture(session: T) {
        CarcorderLog.d(TAG, "stopCapture")
        if (!sessions.contains(session)) return
        session.stopCaptureSession()
        sessions.remove(session)
    }

    private fun onCameraDisconnect(camera: CameraDevice) {
        CarcorderLog.d(TAG, "camera lost ${camera.id}")
        closeAllSession(camera)
        cameras.remove(camera.id)
    }

    private fun closeAllSession(camera: CameraDevice) {
        CarcorderLog.d(TAG, "closeAllSession ${camera.id}")
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