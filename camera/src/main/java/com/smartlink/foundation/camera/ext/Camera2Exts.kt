package com.smartlink.foundation.camera.ext

import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Log
import android.util.Size
import kotlin.math.abs

internal fun CameraCharacteristics.printCameraInfo(tag: String) {
    Log.i(tag, "printCameraInfo: ------ start -------")
    val facing = this.get(CameraCharacteristics.LENS_FACING)
    Log.i(tag, "printCameraInfo: facing ${facing?.getFacingDescription()}")

    val hardwareLevel = this.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
    Log.i(tag, "printCameraInfo: hardware level ${hardwareLevel?.getHardwareLevel()}")

    val orientation = this.get(CameraCharacteristics.SENSOR_ORIENTATION)
    Log.i(tag, "printCameraInfo: sensor orientation $orientation")

    val streamConfigMap = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val sizes = streamConfigMap?.getSupportedTypeSize(SurfaceTexture::class.java)
    Log.i(tag, "printCameraInfo: supportedSize ${sizes?.joinToString(",")}")
    Log.i(tag, "printCameraInfo: support YUV ${streamConfigMap?.isOutputSupportedFor(ImageFormat.YUV_420_888)}")
    Log.i(tag, "printCameraInfo: support NV21 ${streamConfigMap?.isOutputSupportedFor(ImageFormat.NV21)}")

    Log.i(tag, "printCameraInfo: ------ end -------")
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

/**
 * 对于使用SurfaceTexture接收Camera数据的场景
 * 渲染时会通过SurfaceTexture#transform矩阵调整纹理坐标成设备的自然方向，
 * 在实际的渲染计算时，如果相机的方向旋转角度为90的奇数倍，需要将宽高的数值替换后运算
 * 一般情况下，摄像头的旋转角度 90、0
 */
internal fun CameraCharacteristics.isCameraRotate(): Boolean {
    val orientation = this.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
    return orientation / 90 % 2 != 0
}

internal fun CameraCharacteristics.getPreferredCaptureSize(desireSize: Size): Size {
    val streamConfigMap = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val supportedSizes = streamConfigMap?.getSupportedTypeSize(SurfaceTexture::class.java)
    supportedSizes ?: return Size(-1, -1)
    val targetRatio = desireSize.width / desireSize.height.toFloat()
    val ratioReflects = mutableMapOf<Float, MutableList<Size>>()
    supportedSizes.filter { desireSize.width <= it.width  }.forEach {
        val ratio = it.width / it.height.toFloat()
        ratioReflects.putIfAbsent(ratio, mutableListOf())
        ratioReflects[ratio]?.add(it)
    }
    ratioReflects[targetRatio]?.apply {
        return this[findClosestIndex(desireSize.width.toFloat(), this.map { it.width.toFloat() })]
    }
    if (ratioReflects.isEmpty()) return Size(-1, -1)
    val ratios = ratioReflects.keys.toList()
    val closestRatioIndex = findClosestIndex(targetRatio, ratios)
    ratioReflects[ratios[closestRatioIndex]]?.apply {
        return this[findClosestIndex(desireSize.width.toFloat(), this.map { it.width.toFloat() })]
    }
    return Size(-1, -1)
}

private fun findClosestIndex(number: Float, numbers: List<Float>): Int {
    var i = -1
    var wGap = Float.MAX_VALUE
    numbers.forEachIndexed { index, it ->
        val gap = abs (number - it)
        if (gap < wGap) {
            i = index
            wGap = gap
        }
    }
    return i
}