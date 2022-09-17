package cn.shawn.camerapractise.egl

import android.content.Context
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.example.opengl.util.AssetsUtils
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.toFloatBuffer

@Suppress("LeakingThis")
abstract class BaseGlRender(
    protected val context: Context,
    private val vertexAssetPath: String,
    private val fragmentAssetPath: String
) {

    private lateinit var glThreadHandler: Handler

    protected lateinit var eglEnvironment: EglEnvironment

    protected var glProgramId = -1

    private val vertexPositionBuffer = getVertexPosition().toFloatBuffer()

    private val fragmentPositionBuffer = getFragmentPosition().toFloatBuffer()

    private fun initGlThread() {
        val glThread = HandlerThread("EglThread").apply { start() }
        glThreadHandler = Handler(glThread.looper)
    }

    protected fun runOnGlThread(task: () -> Unit) {
        glThreadHandler.post(task)
    }

    private fun initOpenGl() {
        //
        glProgramId = GLES20.glCreateProgram()
        val vertexCode = AssetsUtils.loadString(context, vertexAssetPath) ?: ""
        val fragmentCode = AssetsUtils.loadString(context, fragmentAssetPath) ?: ""
        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        GLES20.glAttachShader(glProgramId, vertexShader)
        GLES20.glAttachShader(glProgramId, fragmentShader)
        GLES20.glLinkProgram(glProgramId)

        GLES20.glUseProgram(glProgramId)
        //
        val vertexPositionLocation = GLES20.glGetAttribLocation(glProgramId, getVertexPositionName())
        GLES20.glEnableVertexAttribArray(vertexPositionLocation)
        GLES20.glVertexAttribPointer(vertexPositionLocation, getVertexPositionStride(),
            GLES20.GL_FLOAT, false, 0, vertexPositionBuffer)

        val fragmentPositionLocation = GLES20.glGetAttribLocation(glProgramId, getFragmentPositionName())
        GLES20.glEnableVertexAttribArray(fragmentPositionLocation)
        GLES20.glVertexAttribPointer(fragmentPositionLocation, getFragmentPositionStride(),
            GLES20.GL_FLOAT, false, 0, fragmentPositionBuffer)

        //设置清屏色值
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    fun createOESTexture(): Int = ShaderUtils.createOESTexture()

    fun createImageTexture(): Int = ShaderUtils.createImageTexture()

    fun makeCurrentEglSurface(eglSurface: EGLSurface) = eglEnvironment.makeCurrentSurface(eglSurface)

    open fun onDraw() {
    }

    /** 定点坐标数组 */
    abstract fun getVertexPosition(): FloatArray

    /** 纹理坐标数组 */
    abstract fun getFragmentPosition(): FloatArray

    abstract fun getVertexPositionStride(): Int

    abstract fun getFragmentPositionStride(): Int

    /** 定点坐标在 GLSL 中声明的名字 */
    abstract fun getVertexPositionName(): String

    /** 纹理坐标在 GLSL 中声明的名字 */
    abstract fun getFragmentPositionName(): String

    init {
        //初始化GL线程
        initGlThread()
        runOnGlThread {
            //初始化EGL环境
            eglEnvironment = EglEnvironment()
            eglEnvironment.init()
            //初始化OpenGl程序
            initOpenGl()
            Log.d("CameraRender", "init egl")
        }

    }

}