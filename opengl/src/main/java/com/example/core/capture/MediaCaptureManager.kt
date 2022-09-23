package com.example.core.capture

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.core.capture.photo.PhotoCapture
import com.example.core.capture.video.VideoRecorder
import com.example.core.entity.config.RecordConfig

class MediaCaptureManager(private val context: Context) {

    private val photoCapture = PhotoCapture()

    private var recordConfig = RecordConfig(false, 3 * 60)

    private var currentVideoRecorder: VideoRecorder? = null

    var onRecordSurfaceCreated: (Surface) -> Unit = {}

    fun insertPhotoCaptureRequest(request: PhotoCapture.Request) {
        photoCapture.insert(request)
    }

    fun pollPhotoCaptureRequest(): PhotoCapture.Request? {
        return photoCapture.poll()
    }

    fun handlePhotoCaptureResult(request: PhotoCapture.Request, result: Result<Bitmap>) {
        photoCapture.handleCapture(request, result)
    }

    fun startVideoRecord(path: String) {
        stopVideoRecord()
        val videoRecorder = VideoRecorder(context,
            VideoRecorder.VideoConfig(recordConfig.videoWidth, recordConfig.videoHeight), path)
        videoRecorder.prepare()
        onRecordSurfaceCreated(videoRecorder.recordSurface)
        videoRecorder.start()
        currentVideoRecorder = videoRecorder
    }

    fun stopVideoRecord() {
        currentVideoRecorder?.apply {
            this.stop()
            this.release()
        }
        currentVideoRecorder = null
    }

}