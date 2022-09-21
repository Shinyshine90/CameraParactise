package cn.shawn.camerapractise.test

import android.content.Context
import android.opengl.GLES20
import android.os.SystemClock
import android.util.Log
import com.example.core.opengl.base.BaseTexturesGlRenderer
import com.example.core.util.GlCoordinates

class TexturesGlRender(context: Context): BaseTexturesGlRenderer(context,
    "shader/textures/vertex.glsl", "shader/textures/fragment.glsl") {

    override fun getTextureBitmapAssets() =
        listOf("textures/image.jpg", "textures/ic_direction.png")

    override fun createVertexCoordinatesList() = listOf(
        GlCoordinates.createNormalVertexPositions(),
        floatArrayOf( -.1f, -.1f, .1f, -.1f, -.1f, .1f, .1f, .1f)
    )

    override fun createTextureCoordinatesList() = listOf(GlCoordinates.createReversedTexturePositions())

    override fun onDraw(width: Int, height: Int) {
        val stamp = SystemClock.elapsedRealtime()
        setVertexAttributeLocation("a_vertexCoors", vertexCoordinatesBuffer[0])
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[0])
        bindTextureToSample("u_texture", textures[0])
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        setVertexAttributeLocation("a_vertexCoors", vertexCoordinatesBuffer[1])
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[0])
        bindTextureToSample("u_texture", textures[1])
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        Log.e("TexturesGlRender", "onDraw: cost ${SystemClock.elapsedRealtime() - stamp}")
    }
}