package com.example.core.opengl

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message

class OpenGlThread {

    val handler = HandlerThread("GlThread@$this").run {
        start()
        GlHandler(looper)
    }

    fun execute(executable: () -> Unit) {
        handler.execute(executable)
    }

    fun release() {
        handler.removeTasks()
        handler.looper.quit()
    }

    class GlHandler(looper: Looper) : Handler(looper) {

        override fun dispatchMessage(msg: Message) {
            if (msg.what == WHAT_MSG) {
                (msg.obj as? Runnable)?.run()
            }
        }

        fun execute(task: Runnable) {
            obtainMessage(WHAT_MSG).apply {
                this.obj = task
            }.sendToTarget()
        }

        fun removeTasks() {
            removeMessages(WHAT_MSG)
        }

        companion object {
            private const val WHAT_MSG = 0x1024
        }
    }
}