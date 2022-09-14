package cn.shawn.camerapractise.camera2

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.util.Log
import android.util.Size

fun CameraCharacteristics.printCameraInfo(tag: String) {
    Log.d(tag, "printCameraInfo: ------ start -------")

    val facing = this.get(CameraCharacteristics.LENS_FACING)
    Log.d(tag, "printCameraInfo: facing ${facing?.getFacingDescription()}")

    val hardwareLevel = this.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
    Log.d(tag, "printCameraInfo: hardware level ${hardwareLevel?.getHardwareLevel()}")

    val streamConfigMap = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val sizes = streamConfigMap?.getSupportedTypeSize(ImageReader::class.java)
    Log.d(tag, "printCameraInfo: supportedSize ${sizes?.joinToString(",")}")
    Log.d(tag, "printCameraInfo: support YUV ${streamConfigMap?.isOutputSupportedFor(ImageFormat.YUV_420_888)}")
    Log.d(tag, "printCameraInfo: support NV21 ${streamConfigMap?.isOutputSupportedFor(ImageFormat.NV21)}")

    Log.d(tag, "printCameraInfo: ------ end -------")
}

private fun Int.getFacingDescription(): String {
    return when (this) {
        CameraCharacteristics.LENS_FACING_FRONT -> "Facing Front"
        CameraCharacteristics.LENS_FACING_BACK -> "Facing Back"
        else -> "External"
    }
}

private fun Int.getHardwareLevel(): String {
    return when(this) {
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "LEVEL_3"
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "FULL"
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "LIMITED"
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "LEGACY"
        else -> "UNKNOWN"
    }
}

/**
 * ImageReader：常用来拍照或接收 YUV 数据。
 * MediaRecorder：常用来录制视频。
 * MediaCodec：常用来录制视频。
 * SurfaceHolder：常用来显示预览画面。
 * SurfaceTexture：常用来显示预览画面。
 */
private fun StreamConfigurationMap.getSupportedTypeSize(clz: Class<*>): List<Size> {
    return this.getOutputSizes(clz)?.toList() ?: arrayListOf()
}

private fun StreamConfigurationMap.isSupportYUV():Boolean {
     return this.isOutputSupportedFor(ImageFormat.YUV_420_888)
}