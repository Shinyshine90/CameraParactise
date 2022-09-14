package cn.shawn.camerapractise.camera2

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.opengl.util.AssetsUtils
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.toFloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(private val context: Context) : GLSurfaceView.Renderer {

    private val vertexPositionBuffer = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    ).toFloatBuffer()

    private val texturePositionBuffer = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    ).toFloatBuffer()

    private val glProgram = GLES20.glCreateProgram()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glAttachShader(
            glProgram,
            ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, AssetsUtils.loadString(context, "shader/camera_vertex.glsl") ?: "")
        )
        GLES20.glAttachShader(
            glProgram,
            ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, AssetsUtils.loadString(context, "shader/camera_fragment.glsl") ?: "")
        )
        GLES20.glLinkProgram(glProgram)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {

    }

}