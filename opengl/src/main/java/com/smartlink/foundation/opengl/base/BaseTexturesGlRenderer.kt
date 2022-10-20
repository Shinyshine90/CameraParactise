package com.smartlink.foundation.opengl.base

import android.content.Context
import android.opengl.GLES20
import com.smartlink.foundation.opengl.utils.AssetsUtils
import com.smartlink.foundation.opengl.utils.GLUtils
import com.smartlink.foundation.opengl.utils.ext.toFloatBuffer

/**
 * Shader Render that supports to draw geometry and textures
 * @param context Android Context
 * @param vertexAssetPath vertex shader glsl code path in assets
 * @param fragmentAssetPath fragment shader glsl code path in assets
 */
abstract class BaseTexturesGlRenderer(
    context: Context,
    vertexAssetPath: String,
    fragmentAssetPath: String
) : BaseShapeGlRenderer(context, vertexAssetPath, fragmentAssetPath) {

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
            val texture = GLUtils.createImageTexture()
            GLUtils.loadBitmapToImageTexture(texture, AssetsUtils.loadBitmap(context, it))
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

    protected fun bindTextureToSample(samplerName: String, texture: Int) {
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