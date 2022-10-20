package com.smartlink.foundation.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import com.smartlink.foundation.camera.ext.getPreferredCaptureSize
import com.smartlink.foundation.camera.ext.isCameraRotate
import com.smartlink.foundation.camera.ext.printCameraInfo
import com.smartlink.foundation.camera.session.BaseCaptureSession
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet

@SuppressLint("MissingPermission")
class CamerasManager(
    private val context: Context,
    private val callbackHandler: Handler = Handler(Looper.getMainLooper()),
    private val logTag: String = "CamerasManager"
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

    fun printCameraInfo(cameraId: String, logTag: String) {
        try {
            cameraManager.getCameraCharacteristics(cameraId).printCameraInfo(logTag)
        } catch (e: CameraAccessException) {
            Log.e(logTag, "printCameraInfo: error")
        }
    }

    fun getPreferredCaptureSize(cameraId: String, desire: Size): Size {
        return cameraManager.getCameraCharacteristics(cameraId).getPreferredCaptureSize(desire)
    }

    fun isCameraRotate(cameraId: String): Boolean {
        return cameraManager.getCameraCharacteristics(cameraId).isCameraRotate()
    }

    fun openCamera(cameraId: String, callback: (Result<CameraDevice>) -> Unit) {
        Log.i(logTag, "openCamera $cameraId")
        val stateCallback = object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {
                cameras[camera.id] = camera
                callback(Result.success(camera))
                Log.i(logTag, "onOpened: $cameraId")
            }

            override fun onDisconnected(camera: CameraDevice) {
                onCameraDisconnect(camera)
                Log.i(logTag, "onDisconnected: $cameraId")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                callback(Result.failure(RuntimeException("camera error, $error")))
                Log.i(logTag, "onError: $cameraId error $error")
            }
        }
        try {
            cameraManager.openCamera(cameraId, stateCallback, callbackHandler)
        } catch (e: CameraAccessException) {
            callback(Result.failure(e))
            Log.e(logTag, "open camera error", e)
        }
    }

    fun closeCamera(cameraId: String) {
        Log.i(logTag, "close camera $cameraId")
        cameras[cameraId]?.apply {
            closeAllSession(this)
            this.close()
        }
        cameras.remove(cameraId)
    }

    fun <T: BaseCaptureSession> startCapture(session: T) {
        Log.i(logTag, "startCapture")
        if (sessions.contains(session)) return
        session.startCapture { result ->
            Log.i(logTag, "startCapture result ${result.isSuccess} ${result.getOrNull()}")
            result.getOrNull()?.apply {
                sessions.add(session)
            }
        }
    }

    fun <T: BaseCaptureSession> stopCapture(session: T) {
        Log.i(logTag, "stopCapture")
        if (!sessions.contains(session)) return
        session.stopCaptureSession()
        sessions.remove(session)
    }

    private fun onCameraDisconnect(camera: CameraDevice) {
        Log.i(logTag, "camera lost ${camera.id}")
        closeAllSession(camera)
        cameras.remove(camera.id)
    }

    private fun closeAllSession(camera: CameraDevice) {
        Log.i(logTag, "closeAllSession ${camera.id}")
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