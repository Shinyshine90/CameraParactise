package cn.shawn.camerapractise.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.core.CarcorderManager
import com.example.core.util.CarcorderLog
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class PhotoCaptureTest(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())

    fun start(intervalMs: Long, times: Long) {
        if (times < 1) return
        //每秒拍照30次
        handler.postDelayed({
            CarcorderManager.capturePhoto(getPhotoTakePath(context)) {
                CarcorderLog.d("CaptureTest"," result ${it.isSuccess}")
            }
            start(intervalMs, times - 1)
        }, intervalMs)
    }

    init {
        context.getExternalFilesDir("PhotoCapture")?.deleteRecursively()
    }

    companion object {

        private val counter = AtomicInteger(0)

        fun getPhotoTakePath(context: Context): String {
            return File(context.getExternalFilesDir("PhotoCapture"),
                "${counter.getAndIncrement()}.jpg").absolutePath
        }
    }


}