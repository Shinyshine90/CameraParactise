package com.example.core.opengl.base

import android.content.Context
import android.opengl.GLES20
import com.example.core.util.AssetsUtils
import com.example.core.util.GlUtils
import com.example.core.util.ext.toFloatBuffer
import java.nio.Buffer

abstract class BaseShapeGlRenderer(
    protected val context: Context,
    private val vertexAssetPath: String,
    private val fragmentAssetPath: String
) {

    protected var glProgram = 0

    protected val vertexPositionBuffer by lazy {
        createVertexCoordinates().toFloatBuffer()
    }

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

    open fun draw(width: Int, height: Int) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(glProgram)
        onDraw(width, height)
    }

    protected abstract fun onDraw(width: Int, height: Int)

    protected fun setVertexAttributeLocation(name: String, buffer: Buffer) {
        GlUtils.setVertexAttributeLocation(glProgram, name, buffer)
    }

    abstract fun createVertexCoordinates(): FloatArray


}