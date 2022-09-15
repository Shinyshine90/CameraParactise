package cn.shawn.camerapractise.camera2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.util.Log
import cn.shawn.camerapractise.util.GlUtils
import com.example.opengl.util.AssetsUtils
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.toFloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "CameraRender"

class CameraRender(private val context: Context) : GLSurfaceView.Renderer {

    enum class PreviewMode(val mode: Int) {
        NORMAL(0), TOP(1), BOTTOM(2), LEFT(3), RIGHT(4), H(5)
    }

    private val vertexPositionBuffer =
        floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f).toFloatBuffer()

    private val texturePositionBuffer =
        floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f).toFloatBuffer()

    private val cameraMatrix = FloatArray(16)

    private var glProgramId = -1

    var previewMode = PreviewMode.NORMAL

    private val oesTextureId by lazy {
        ShaderUtils.createOESTexture()
    }

    private val oesTexture by lazy {
        SurfaceTexture(oesTextureId).apply {
            setDefaultBufferSize(1080, 1920)
        }
    }

    var onTextureCreated: ((texture: SurfaceTexture) -> Unit)? = null

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glProgramId = GLES20.glCreateProgram()
        val vertexCode = AssetsUtils.loadString(context, "shader/camera_vertex.glsl") ?: ""
        val fragmentCode = AssetsUtils.loadString(context, "shader/camera_fragment.glsl") ?: ""
        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        GLES20.glAttachShader(glProgramId, vertexShader)
        GLES20.glAttachShader(glProgramId, fragmentShader)
        GLES20.glLinkProgram(glProgramId)
        onTextureCreated?.invoke(oesTexture)
        GLES20.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        GLES20.glViewport(0, 0, 1280, 720)
    }

    override fun onDrawFrame(gl10: GL10?) {
        Log.d(TAG, "onDrawFrame: ")
        oesTexture.updateTexImage()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(glProgramId)

        val vertexPosition = GLES20.glGetAttribLocation(glProgramId, "a_vertexPosition")
        GLES20.glEnableVertexAttribArray(vertexPosition)
        vertexPositionBuffer.position(0)
        GLES20.glVertexAttribPointer(
            vertexPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexPositionBuffer
        )

        val texturePosition = GLES20.glGetAttribLocation(glProgramId, "a_texturePosition")
        GLES20.glEnableVertexAttribArray(texturePosition)
        texturePositionBuffer.position(0)
        GLES20.glVertexAttribPointer(
            texturePosition,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            texturePositionBuffer
        )

        val rotateMatrixPosition = GLES20.glGetUniformLocation(glProgramId, "u_rotateMatrix")
        oesTexture.getTransformMatrix(cameraMatrix)
        GLES20.glUniformMatrix4fv(rotateMatrixPosition, 1, false, cameraMatrix, 0)

        //绘制模式
        val previewModeLocation = GLES20.glGetUniformLocation(glProgramId, "u_previewMode")
        GLES20.glUniform1i(previewModeLocation, previewMode.mode)

        //绘制
        GLES20.glActiveTexture(GLES20.GL_TEXTURE30)
        GLES20.glBindTexture(glProgramId, oesTextureId)

        val textureLocation = GLES20.glGetUniformLocation(glProgramId, "u_texture")
        GLES20.glUniform1i(textureLocation, 30)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glBindTexture(glProgramId, 0)
        takePicture(gl10)
    }

    var onTakePicture: ((Bitmap) -> Unit)? = null

    private fun takePicture(gl10: GL10?) {
        gl10 ?: return
        val startStamp = SystemClock.elapsedRealtime()
        val bitmap = GlUtils.getBitmapFromGL(1280, 720, gl10)
        onTakePicture?.invoke(bitmap)
        Log.d(TAG, "takePicture: ${SystemClock.elapsedRealtime() - startStamp}")
    }

}