package cn.shawn.camerapractise.camera1

import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception

private const val TAG = "CameraApi1Helper"

@Suppress("DEPRECATION")
class CameraApi1Helper(private val surfaceView: SurfaceView) {

    private var cameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK

    private var camera: Camera? = null

    private val availableCameras = SparseArray<Camera.CameraInfo>()

    private val availableCameraCount: Int
        get() = Camera.getNumberOfCameras()


    fun open(id: Int): Boolean {
        return try {
            release()
            camera = Camera.open(id)
            cameraId = id
            Log.d(TAG, "camera params info ${camera?.paramsInfo()}")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun release() {
        execCameraSafe { camera ->
            camera.release()
        }
        this.camera = null
    }

    fun setupCamera(
        previewFps: Int, previewWidth: Int, previewHeight: Int,
        pictureWidth: Int = previewWidth, pictureHeight: Int = previewHeight
    ) {
        execCameraSafe { camera ->
            val params = camera.parameters.apply {
                previewFrameRate = previewFps
                previewFormat = ImageFormat.NV21
                pictureFormat = ImageFormat.JPEG
                setPreviewSize(previewWidth, previewHeight)
                setPictureSize(pictureWidth, pictureHeight)

                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    this.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                }

                camera.setDisplayOrientation(OrientationUtils.getCameraDisplayOrientation(surfaceView.context, cameraId, camera))
            }
            camera.parameters = params
        }
    }

    fun startPreview() {
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                execCameraSafe { camera ->
                    camera.setPreviewDisplay(surfaceView.holder)
                    camera.startPreview()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })

    }

    fun takePicture() {
        execCameraSafe { camera ->
            camera.takePicture(null, { data: ByteArray?, _ ->
                Log.d(TAG, "takePicture: raw data ${data.contentToString()}")
            }) { jpegData: ByteArray?, _ ->
                execCameraSafe { it.startPreview() }
                Log.d(TAG, "takePicture: jepg data ${jpegData.contentToString()}")
            }
        }
    }


    fun getFrontFacingCameraId(): Int? {
        for (i in 0 until availableCameras.size()) {
            if (availableCameras.valueAt(i).isFacingFront()) {
                return availableCameras.keyAt(i)
            }

        }
        return null
    }

    fun getBackFacingCameraId(): Int? {
        for (i in 0 until availableCameras.size()) {
            if (availableCameras.valueAt(i).isFacingBack()) {
                return availableCameras.keyAt(i)
            }

        }
        return null
    }

    private fun initCameraInfo(array: IntArray = IntArray(availableCameraCount) { i -> i }) {
        array.forEach { cameraId ->
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            availableCameras[cameraId] = cameraInfo
            Log.d(TAG, "printCameraInfo: cameraId=$cameraId, cameraInfo=${cameraInfo.info()}")
        }
    }

    private inline fun execCameraSafe(task: (Camera) -> Unit) {
        this.camera?.apply(task)
    }

    init {
        initCameraInfo()
    }

}