package cn.shawn.camerademo

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.view.Surface

private const val TAG = "VideoRecorder"

internal class VideoRecorder(context: Context, config: VideoConfig, filePath: String) {

    private val mediaRecorder: MediaRecorder? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            DiskUtils.prepareFile(filePath)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(filePath)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoEncodingBitRate(config.bitRate)
            setVideoSize(config.width, config.height)
            setVideoFrameRate(config.frameRate)
            setOrientationHint(0)
            Log.i(TAG, "init success")
        }
    } catch (e: Exception) {
        Log.e(TAG, "init failed", e)
        null
    }

    /** acquire this after prepare **/
    val recordSurface: Surface?
        get() = try {
            requireMediaRecorder().surface
        } catch (e: Exception) {
            Log.e(TAG, "create recorder surface failed", e)
            null
        }

    fun prepare() {
        try {
            requireMediaRecorder().prepare()
            Log.i(TAG, "prepare success")
        } catch (e: Exception) {
            Log.e(TAG, "prepare error", e)
        }
    }

    fun start() {
        try {
            requireMediaRecorder().start()
            Log.i(TAG, "start success")
        } catch (e: Exception) {
            Log.e(TAG, "start error", e)
        }
    }

    fun stop() {
        try {
            requireMediaRecorder().stop()
            Log.i(TAG, "stop success")
        } catch (e: Exception) {
            Log.e(TAG, "stop error", e)
        }
    }

    fun release() {
        try {
            requireMediaRecorder().release()
            Log.i(TAG, "release success")
        } catch (e: Exception) {
            Log.e(TAG, "release error", e)
        }
    }

    private fun requireMediaRecorder(): MediaRecorder = mediaRecorder!!

    data class VideoConfig(
        val width: Int,
        val height: Int,
        val bitRate: Int = width * height * 2,
        val frameRate: Int = 30
    )
}