package com.example.core.opengl.base

import android.content.Context
import android.opengl.GLES20
import com.example.core.util.AssetsUtils
import com.example.core.util.GlUtils
import com.example.core.util.ext.toFloatBuffer

abstract class BaseTexturesGlRender(
    context: Context,
    vertexAssetPath: String,
    fragmentAssetPath: String
) : BaseOpenGlRender(context, vertexAssetPath, fragmentAssetPath) {

    protected val vertexCoordinatesBuffer by lazy {
        createVertexCoordinatesList().map {
            it.toFloatBuffer()
        }
    }

    protected val textureCoordinatesBuffer by lazy {
        createTextureCoordinatesList().map {
            it.toFloatBuffer()
        }
    }

    protected val textures by lazy {
        getTextureBitmapAssets().map {
            val texture = GlUtils.createImageTexture()
            GlUtils.loadBitmapToImageTexture(texture, AssetsUtils.loadBitmap(context, it))
            texture
        }.toList()
    }

    override fun initGlProgram() {
        super.initGlProgram()
        //初始化纹理对应的定点、纹理坐标
        vertexCoordinatesBuffer
        textureCoordinatesBuffer
        //初始化、绑定纹理
        textures
    }

    protected fun bindTextureToSample(texture: Int, samplerName: String) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE30)
        val locationSampler = GLES20.glGetUniformLocation(glProgram, samplerName)
        GLES20.glUniform1i(locationSampler,30)
    }

    abstract fun getTextureBitmapAssets(): List<String>

    abstract fun createVertexCoordinatesList(): List<FloatArray>

    abstract fun createTextureCoordinatesList(): List<FloatArray>

    override fun createVertexCoordinates(): FloatArray = floatArrayOf()
}