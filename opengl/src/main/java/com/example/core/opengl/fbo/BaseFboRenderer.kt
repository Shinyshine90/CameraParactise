package com.example.core.opengl.fbo

import android.content.Context
import android.opengl.GLES20
import com.example.core.util.AssetsUtils
import com.example.core.util.GlUtils
import com.example.core.util.ext.toFloatBuffer
import java.nio.Buffer

abstract class BaseFboRenderer(
    protected val context: Context,
    private val vertexAssetPath: String,
    private val fragmentAssetPath: String
) {

    protected var glProgram = 0

    protected val vertexCoordinateBuffer by lazy {
        createVertexCoordinates().toFloatBuffer()
    }

    protected val textureCoordinateBuffer by lazy {
        createTextureCoordinates().toFloatBuffer()
    }

    private var clearColor = FloatArray(4) { -1f }

    private var hasBufferInit: Boolean = false

    var frameBuffer: Int = -1
        private set

    var frameBufferTexture: Int = -1
        private set

    open fun initGlProgram() {
        //创建GL程序
        glProgram = GLES20.glCreateProgram()
        //编译、链接着色器程序
        val vertexCode = AssetsUtils.loadString(context, vertexAssetPath)
        val fragmentCode = AssetsUtils.loadString(context, fragmentAssetPath)
        GlUtils.loadShader(glProgram, GLES20.GL_VERTEX_SHADER, vertexCode)
        GlUtils.loadShader(glProgram, GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        //链接程序
        GlUtils.linkProgram(glProgram)
    }

    open fun setSurfaceSize(width: Int, height: Int) {
        if (!hasBufferInit) {
            GlUtils.createFrameBuffer(width, height).apply {
                this@BaseFboRenderer.frameBuffer = this.first
                this@BaseFboRenderer.frameBufferTexture = this.second
            }
            hasBufferInit = true
        }
    }

    open fun draw(width: Int, height: Int, texture: Int, frameBuffer: Int, index: Int, chain:FboRendererChain) {
        GLES20.glUseProgram(glProgram)
        GLES20.glViewport(0, 0, width, height)
        onDraw(width, height, texture, frameBuffer, chain)
        chain.processRender(width, height, frameBufferTexture, index + 1)
    }

    protected abstract fun onDraw(width: Int, height: Int, texture: Int, frameBuffer: Int, chain:FboRendererChain)

    protected fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        clearColor[0] = r
        clearColor[1] = g
        clearColor[2] = b
        clearColor[3] = a
    }

    protected fun clearSurface() {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    protected fun bindTextureToSample(samplerName: String, type: Int, texture: Int) {
        GLES20.glBindTexture(type, texture)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        val locationSampler = GLES20.glGetUniformLocation(glProgram, samplerName)
        GLES20.glUniform1i(locationSampler,0)
    }

    protected fun setVertexAttributeLocation(name: String, buffer: Buffer) {
        GlUtils.setVertexAttributeLocation(glProgram, name, buffer)
    }

    abstract fun createVertexCoordinates(): FloatArray

    abstract fun createTextureCoordinates(): FloatArray
}