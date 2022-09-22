package cn.shawn.camerapractise.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.core.CarcorderManager
import com.example.core.util.CarcorderLog
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class CaptureTest(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())

    fun start(intervalMs: Long) {
        //每秒拍照30次
        handler.postDelayed({
            CarcorderManager.capturePhoto(getFilePath(context)) {
                CarcorderLog.d("CaptureTest"," result ${it.isSuccess}")
            }
            start(intervalMs)
        }, intervalMs)
    }

    companion object {

        private val counter = AtomicInteger(0)

        fun getFilePath(context: Context): String {
            return File(context.getExternalFilesDir("Capture"),
                "${counter.getAndIncrement()}.jpg").absolutePath
        }
    }


}