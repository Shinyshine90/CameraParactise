package com.example.core.capture

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

    val recordSurface: Surface
        get() = mediaRecorder.surface

    fun prepare() {
        try {
            mediaRecorder.prepare()
        } catch (e: Exception) {
            CarcorderLog.d(TAG, "prepare error ${e.message}")
        }
    }

    fun start() {
        try {
            mediaRecorder.start()
        } catch (e: Exception) {
            CarcorderLog.d(TAG, "start error ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaRecorder.stop()
        } catch (e: Exception) {
            CarcorderLog.d(TAG, "stop error ${e.message}")
        }
    }
    data class VideoConfig(
        val width: Int,
        val height: Int,
        val bitRate: Int,
        val frameRate: Int
    )
}