package cn.shawn.camerapractise.recorder

import android.content.Context
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import cn.shawn.camerapractise.util.TimeTicker
import java.io.File
import java.lang.Exception

@Suppress("DEPRECATION")
class VideoRecorder(private val context: Context) {

    private val mediaRecorder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setOnErrorListener { mr, what, extra ->
                Log.e("VideoRecorder", "what=$what extra=$extra", )
            }
        }
    }

    private val timer by lazy {
        TimeTicker(10000)
    }

    private fun configRecorder() {
        mediaRecorder.apply {
            //setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            //setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoSize(1280, 720)
            setVideoEncodingBitRate(8 * 1280 * 720)
            //setOrientationHint()
            setOutputFile(getOutputFilePath().absolutePath)
        }
    }

    fun startIntervalRecorder(camera: Camera) {
        timer.stop()
        timer.start {
            stopRecorder(camera)
            startRecorder(camera)
        }
    }

    fun startRecorder(camera: Camera) {
        camera.unlock()
        mediaRecorder.setCamera(camera)
        configRecorder()
        mediaRecorder.prepare()
        mediaRecorder.start()
    }

    fun stopRecorder(camera: Camera) {
        try {
            camera.reconnect()
            mediaRecorder.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getOutputFilePath() =
        File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "${System.currentTimeMillis()}.mp4")

}