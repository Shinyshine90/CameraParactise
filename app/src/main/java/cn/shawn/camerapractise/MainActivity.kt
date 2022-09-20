package cn.shawn.camerapractise

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.shawn.camerapractise.databinding.ActivityMainBinding
import cn.shawn.camerapractise.test.TexturesGlRender
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "MainActivityTAG"

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        findViewById<GLSurfaceView>(R.id.sv).apply {
            setEGLContextClientVersion(2)
            setRenderer(SimpleRender())
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    inner class SimpleRender: GLSurfaceView.Renderer {

        private val render = TexturesGlRender(this@MainActivity)

        private var width = 0

        private var height = 0

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            render.initGlProgram()
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            this.width = width
            this.height = height
        }

        override fun onDrawFrame(gl: GL10?) {
            render.draw(width, height)
        }

    }

}