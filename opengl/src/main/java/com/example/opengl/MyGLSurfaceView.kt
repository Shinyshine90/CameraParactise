package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.example.opengl.shape.BitmapTexture
import com.example.opengl.shape.RenderMode
import com.example.opengl.shape.SimpleTexture
import com.example.opengl.shape.Square
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "MyGLSurfaceView"

class MyGLSurfaceView constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private val render = MyRender()

    private lateinit var square: Square

    private lateinit var texture: BitmapTexture

    private lateinit var simpleTexture: SimpleTexture

    init {
        setEGLContextClientVersion(2)
        setRenderer(render)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return super.dispatchTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                updateMode(RenderMode.FULL)
                return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun updateMode(renderMode: RenderMode) {
        render.mode = renderMode
    }

    inner class MyRender : Renderer {

        private var scale = 1f

        var mode = RenderMode.FULL
            set(value) {
                Log.d(TAG, "update mode : $value")
                field = value
            }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(1f, 1f, 1f, 1f)
            //square = Square()
            //texture = BitmapTexture(context)
            simpleTexture = SimpleTexture(context)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            scale = width.toFloat() / height
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            //square.draw(scale)
            //texture.draw()
            simpleTexture.draw(mode)
        }
    }


}