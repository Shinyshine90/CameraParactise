package com.example.core.opengl.fbo

import android.content.Context
import com.example.core.util.AssetsUtils
import com.example.core.util.GlUtils
import com.example.core.util.ext.toFloatBuffer

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
        imageTextures
    }

    abstract fun getTextureBitmapAssets(): List<String>

    abstract fun createVertexCoordinatesList(): List<FloatArray>

    abstract fun createTextureCoordinatesList(): List<FloatArray>

    override fun createVertexCoordinates(): FloatArray = floatArrayOf()

    override fun createTextureCoordinates(): FloatArray = floatArrayOf()

 }