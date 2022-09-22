package cn.shawn.camerapractise.test.render.fbo

import android.content.Context
import android.opengl.GLES20
import com.example.core.opengl.fbo.BaseTexturesFboRenderer
import com.example.core.opengl.fbo.FboRendererChain
import com.example.core.util.GlCoordinates

class FboRenderer1(context: Context): BaseTexturesFboRenderer(context, "shader/textures/vertex.glsl", "shader/textures/fragment.glsl") {

    override fun getTextureBitmapAssets() = listOf("textures/ic_direction.png")

    override fun createVertexCoordinatesList() = listOf(
        GlCoordinates.createNormalVertexPositions(),
        floatArrayOf(-0.1f, -.1f, .1f, -.1f, -.1f, .1f, .1f, .1f)
    )

    override fun createTextureCoordinatesList() = listOf(GlCoordinates.createReversedTexturePositions())

    override fun onDraw(width: Int, height: Int, textureId: Int, frameBuffer: Int, chain: FboRendererChain) {
        setVertexAttributeLocation("a_vertexCoors", vertexCoordinatesBuffer[0])
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[0])
        bindTextureToSample("u_texture",GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)


        setVertexAttributeLocation("a_vertexCoors", vertexCoordinatesBuffer[1])
        setVertexAttributeLocation("a_textureCoors", textureCoordinatesBuffer[0])
        bindTextureToSample("u_texture",GLES20.GL_TEXTURE_2D, imageTextures[0])
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

}