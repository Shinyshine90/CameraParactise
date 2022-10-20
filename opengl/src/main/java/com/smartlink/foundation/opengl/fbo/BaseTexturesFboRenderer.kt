package com.smartlink.foundation.opengl.fbo

import android.content.Context
import com.smartlink.foundation.opengl.utils.AssetsUtils
import com.smartlink.foundation.opengl.utils.GLUtils
import com.smartlink.foundation.opengl.utils.ext.toFloatBuffer

/**
 * extends from BasicFboRenderer
 * provide the convenient operation for handle texture
 */
abstract class BaseTexturesFboRenderer(
    context: Context,
    vertexAssetPath: String,
    fragmentAssetPath: String
): BaseFboRenderer(context, vertexAssetPath, fragmentAssetPath) {

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

    protected val imageTextures by lazy {
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
        imageTextures
    }

    abstract fun getTextureBitmapAssets(): List<String>

    abstract fun createVertexCoordinatesList(): List<FloatArray>

    abstract fun createTextureCoordinatesList(): List<FloatArray>

    override fun createVertexCoordinates(): FloatArray = floatArrayOf()

    override fun createTextureCoordinates(): FloatArray = floatArrayOf()

 }