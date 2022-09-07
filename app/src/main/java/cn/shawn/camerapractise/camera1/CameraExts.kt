@file:Suppress("DEPRECATION")

package cn.shawn.camerapractise.camera1

import android.hardware.Camera

fun Camera.CameraInfo.isFacingFront() =
    this.facing == Camera.CameraInfo.CAMERA_FACING_FRONT

fun Camera.CameraInfo.isFacingBack() =
    this.facing == Camera.CameraInfo.CAMERA_FACING_BACK

fun Camera.CameraInfo.info() = this.run {
    "{CameraInfo: facing=$facing, orientation=$orientation, canDisableShutterSound=$canDisableShutterSound}"
}

fun Camera.paramsInfo() = this.parameters.run {
    """
        {
            supportedPreviewFormat = ${supportedPreviewFormat()},
            supportedPreviewInfo = ${supportedPreviewInfo()},
            supportedPictureFormat = ${supportedPictureFormat()}            ,
            supportedPictureInfo = ${supportedPictureInfo()},
        }
    """.trimIndent()
}

fun Camera.Parameters.supportedPictureFormat() = this.run {
    this.supportedPictureFormats.joinToString("\r\n", prefix = "\r\n", postfix = "\r\n"){ "{PictureFormat: $it" }
}

fun Camera.Parameters.supportedPreviewFormat() = this.run {
    this.supportedPreviewFormats.joinToString("\r\n", prefix = "\r\n", postfix = "\r\n"){ "{PreviewFormat: $it" }
}

fun Camera.Parameters.supportedPreviewInfo() = this.run {
    this.supportedPreviewSizes.joinToString("\r\n", prefix = "\r\n", postfix = "\r\n"){ "{CameraSize: width=${it.width}, height=${it.height}}" }
}

fun Camera.Parameters.supportedPictureInfo() = this.run {
    this.supportedPictureSizes.joinToString("\r\n", prefix = "\r\n", postfix = "\r\n"){ "{CameraSize: width=${it.width}, height=${it.height}}" }
}
