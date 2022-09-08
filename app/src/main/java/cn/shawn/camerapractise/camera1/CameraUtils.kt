package cn.shawn.camerapractise.camera1

import android.content.Context
import android.hardware.Camera
import android.view.Surface
import android.view.WindowManager

@Suppress("DEPRECATION")
object CameraUtils {

    fun getCameraDisplayOrientation(context: Context, cameraId: Int): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation

        var screenDegree = 0
        when (rotation) {
            Surface.ROTATION_0 -> screenDegree = 0
            Surface.ROTATION_90 -> screenDegree = 90
            Surface.ROTATION_180 -> screenDegree = 180
            Surface.ROTATION_270 -> screenDegree = 270
        }
        var displayOrientation: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + screenDegree) % 360
            displayOrientation = (360 - displayOrientation) % 360          // compensate the mirror
        } else {
            displayOrientation = (info.orientation - screenDegree + 360) % 360
        }
        return displayOrientation
    }
}