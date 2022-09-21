package com.example.core.opengl.render

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import android.util.SparseArray
import com.example.core.entity.RenderMode
import com.example.core.opengl.fbo.BaseTexturesFboRenderer
import com.example.core.opengl.fbo.FboRendererChain
import com.example.core.util.GlCoordinates
import java.nio.Buffer

class CameraWaterMarkRender(context: Context): BaseTexturesFboRenderer(
    context,
    "shader/camera/marker/vertex.glsl",
    "shader/camera/marker/fragment.glsl"
) {

    private val markerWidth = 100

    private val markerHeight = 100

    private val surfaceWidth = 1280f

    private val surfaceHeight = 720f

    private val scaleHalfWidth = markerWidth / 2f / surfaceWidth

    private val scaleHalfHeight = markerHeight / 2f / surfaceHeight

    private val standardPosition = floatArrayOf(0f, -scaleHalfHeight * 1.2f)

    private val renderReflects = SparseArray<List<Pair<Int,Int>>>().apply {
        put(RenderMode.SINGLE_SIDE_FRONT.mode, listOf(Pair(0, 2)))
        put(RenderMode.SINGLE_SIDE_BACK.mode, listOf(Pair(1, 2)))
        put(RenderMode.SINGLE_SIDE_LEFT.mode, listOf(Pair(2, 2)))
        put(RenderMode.SINGLE_SIDE_RIGHT.mode, listOf(Pair(3, 2)))

        put(RenderMode.DOUBLE_SIDE_LR_FAIRLY.mode, listOf(Pair(2, 4), Pair(3, 5)))
        put(RenderMode.DOUBLE_SIDE_LR_LEFT_WEIGHT.mode, listOf(Pair(2, 2), Pair(3, 6)))
        put(RenderMode.DOUBLE_SIDE_LR_RIGHT_WEIGHT.mode, listOf(Pair(2, 3), Pair(3, 2)))

        put(RenderMode.TRIPLE_SIDE_LRB.mode, listOf(Pair(1, 2),Pair(2, 3), Pair(3, 6)))

        put(RenderMode.FOUR_SIDE_T.mode, listOf(Pair(0, 4), Pair(1, 5),Pair(2, 7), Pair(3, 8)))
        put(RenderMode.FOUR_SIDE_H.mode, listOf(Pair(0, 2), Pair(1, 1),Pair(2, 3), Pair(3, 6)))
    }

    override fun createTextureCoordinatesList() = listOf(
        GlCoordinates.createNormalTexturePositions(),
        GlCoordinates.createReversedTexturePositions())

    override fun onDraw(
        width: Int,
        height: Int,
        texture: Int,
        frameBuffer: Int,
        chain: FboRendererChain
    ) {
        //绘制Camera OES转换后的2D纹理
        setVertexAttributeLocation("a_vertexCoors", vertexCoordinatesBuffer[0])
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[0])
        bindTextureToSample("u_texture", GLES20.GL_TEXTURE_2D, texture)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 , 4)

        //绘制Marker点
        val renderMode = chain.getTag(RenderMode::class.java)
        renderReflects[renderMode.mode].forEach {
            Log.d("CameraWaterMarkRender", "onDraw: textureId=${imageTextures[it.first]}, " +
                    "vertexIndex=${it.second}")
            drawMarker(imageTextures[it.first], vertexCoordinatesBuffer[it.second], frameBuffer)
        }
    }

    private fun drawMarker(texture: Int, vertexCoorsBuffer: Buffer, frameBuffer: Int) {
        setVertexAttributeLocation("a_vertexCoors", vertexCoorsBuffer)
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[1])
        bindTextureToSample("u_texture", GLES20.GL_TEXTURE_2D, texture)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 , 4)
    }

    private fun createMarkerCoordinate(offsetX: Float, offsetY: Float): FloatArray {
        val x = standardPosition[0] + offsetX
        val y = standardPosition[1] + offsetY
        return floatArrayOf(
            x - scaleHalfWidth, y - scaleHalfHeight,
            x + scaleHalfWidth, y - scaleHalfHeight,
            x - scaleHalfWidth, y + scaleHalfHeight,
            x + scaleHalfWidth, y + scaleHalfHeight
        )
    }

    override fun getTextureBitmapAssets() = listOf(
        "texture/marker/direction/front.png",
        "texture/marker/direction/back.png",
        "texture/marker/direction/left.png",
        "texture/marker/direction/right.png",
    )

    override fun createVertexCoordinatesList() = listOf(
        //camera rgb 坐标
        GlCoordinates.createNormalVertexPositions(),
        // (0, 0) , index 1
        createMarkerCoordinate(0f, 0f),
        // (0, 1), index 2
        createMarkerCoordinate(0f, 1f),
        // (-3/4, 1), index 3
        createMarkerCoordinate(-0.75f, 1f),
        // (-1/2, 1), index 4
        createMarkerCoordinate(-0.5f, 1f),
        //(1/2, 1), index 5
        createMarkerCoordinate(0.5f, 1f),
        //(3/4, 1), index 6
        createMarkerCoordinate(0.75f, 1f),
        //(-1/2, 0), index 7
        createMarkerCoordinate(-0.5f, 0f),
        //(1/2, 0), index 8
        createMarkerCoordinate(0.5f, 0f),
    )
}