package com.example.core.capture.video

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.view.Surface
import com.example.core.util.CarcorderLog

private const val TAG = "VideoRecorder"

class VideoRecorder(context: Context, config: VideoConfig, filePath: String) {

    private val mediaRecorder = kotlin.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(filePath)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoEncodingBitRate(config.bitRate)
            setVideoSize(config.width, config.height)
            setVideoFrameRate(config.frameRate)
            setOrientationHint(0)
        }
    }

    /** acquire this after prepare **/
    val recordSurface: Surface
        get() = mediaRecorder.surface

    fun prepare() {
        try {
            mediaRecorder.prepare()
            CarcorderLog.e(TAG, "prepare success")
        } catch (e: Exception) {
            CarcorderLog.e(TAG, "prepare error ${e.message}")
        }
    }

    fun start() {
        try {
            mediaRecorder.start()
            CarcorderLog.e(TAG, "prepare start success")
        } catch (e: Exception) {
            CarcorderLog.e(TAG, "start error ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaRecorder.stop()
            CarcorderLog.e(TAG, "prepare stop success")
        } catch (e: Exception) {
            CarcorderLog.e(TAG, "stop error ${e.message}")
        }
    }

    fun release() {
        try {
            mediaRecorder.release()
            CarcorderLog.e(TAG, "prepare release success")
        } catch (e: Exception) {
            CarcorderLog.e(TAG, "release error ${e.message}")
        }
    }

    data class VideoConfig(
        val width: Int,
        val height: Int,
        val bitRate: Int = width * height * 4,
        val frameRate: Int = 30
    )
}