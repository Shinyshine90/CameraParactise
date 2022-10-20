package com.smartlink.foundation.opengl.fbo

import android.content.Context
import android.opengl.GLES20
import com.smartlink.foundation.opengl.utils.AssetsUtils
import com.smartlink.foundation.opengl.utils.GLUtils
import com.smartlink.foundation.opengl.utils.ext.toFloatBuffer
import java.nio.Buffer

/**
 * Basic FboRenderer
 * Each FboRenderer holds one frameBuffer and frameBuffer's Texture.
 * Renderer draw procession has bean scheduled by FboRendererChain in order,
 * On the procession draw(), each renderer accept a texture(OES/2D) from last render in chain or
 * the outside input, reproduce the texture and output data to itself frameBuffer.
 * after all of this, call the render chain to schedule the next renderer with itself frameBuffer's Texture.
 */
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
        GLUtils.loadShader(glProgram, GLES20.GL_VERTEX_SHADER, vertexCode)
        GLUtils.loadShader(glProgram, GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        //链接程序
        GLUtils.linkProgram(glProgram)
    }

    open fun createFrameBuffer(width: Int, height: Int) {
        if (!hasBufferInit) {
            GLUtils.createFrameBuffer(width, height).apply {
                this@BaseFboRenderer.frameBuffer = this.first
                this@BaseFboRenderer.frameBufferTexture = this.second
            }
            hasBufferInit = true
        }
    }

    /**
     * @param texture input texture(OES/RGBA)
     * @param frameBuffer output frameBuffer, last renderer should direct to 0 to output to screen
     * @param index current renderer's index in the renderer chain
     */
    open fun draw(texture: Int, frameBuffer: Int, index: Int, chain: FboRendererChain) {
        GLES20.glUseProgram(glProgram)
        onDraw(texture, frameBuffer, chain)
        chain.processRender(frameBufferTexture, index + 1)
    }

    protected abstract fun onDraw(texture: Int, frameBuffer: Int, chain: FboRendererChain)

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
        GLUtils.setVertexAttributeCoors(glProgram, name, buffer)
    }

    abstract fun createVertexCoordinates(): FloatArray

    abstract fun createTextureCoordinates(): FloatArray
}