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

    fun execute(executable: () -> Unit, delay: Long = 0) {
        handler.execute(executable, delay)
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

        fun execute(task: Runnable, delay: Long = 0) {
            val msg = Message.obtain(this, WHAT_MSG).apply {
                this.obj = task
            }
            sendMessageDelayed(msg, delay)
        }

        fun removeTasks() {
            removeMessages(WHAT_MSG)
        }

        companion object {
            private const val WHAT_MSG = 0x1024
        }
    }
}