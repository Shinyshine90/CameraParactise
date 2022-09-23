package cn.shawn.camerapractise.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.core.CarcorderManager
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class VideoCaptureTest(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())

    fun start(intervalMs: Long, times: Long) {
        CarcorderManager.stopVideoRecord()
        if (times < 1) return
        CarcorderManager.startVideoRecord(getVideoTakePath(context))
        handler.postDelayed({
            start(intervalMs, times - 1)
        }, intervalMs)
    }

    init {
        context.getExternalFilesDir("VideoCapture")?.deleteRecursively()
    }

    companion object {

        private val counter = AtomicInteger(0)

        fun getVideoTakePath(context: Context): String {
            return File(context.getExternalFilesDir("VideoCapture"),
                "${counter.getAndIncrement()}.mp4").absolutePath
        }
    }


}