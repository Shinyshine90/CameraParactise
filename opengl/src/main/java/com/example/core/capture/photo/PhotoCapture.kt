package com.example.core.capture.photo

import android.graphics.Bitmap
import com.example.core.util.BitmapUtils
import com.example.core.util.DiskUtils
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class PhotoCapture {

    private val requestQueue = LinkedBlockingQueue<Request>()

    private val ioExecutor = ThreadPoolExecutor(
        0, Int.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        SynchronousQueue()
    )

    fun insert(request: Request) {
        requestQueue.add(request)
    }

    fun poll(): Request? {
        return requestQueue.poll()
    }

    fun handleCapture(request: Request, result: Result<Bitmap>) {
        result.fold({
            ioExecutor.submit {
                var rotateBitmap: Bitmap? = null
                val file = DiskUtils.prepareFile(request.path)
                try {
                    rotateBitmap = BitmapUtils.handleMirrorRotate(it)
                    BitmapUtils.compressToDisk(file, rotateBitmap)
                    request.getCallback()?.invoke(Result.success(file.path))
                } catch (e: Exception) {
                    request.getCallback()?.invoke(Result.failure(e))
                }
                rotateBitmap?.recycle()
                it.recycle()
            }
        },{
            request.getCallback()?.invoke(Result.failure(it))
        })
    }

    fun clear() {
        requestQueue.clear()
    }

    class Request(val path: String, callback: (Result<String>) -> Unit) {

        private val resultCallback = WeakReference(callback)

        fun getCallback() = resultCallback.get()
    }



}