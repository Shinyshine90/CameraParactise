package com.example.core.opengl.render

import android.content.Context
import android.opengl.GLES20
import com.example.core.opengl.fbo.BaseFboRenderer
import com.example.core.opengl.fbo.FboRendererChain
import com.example.core.util.GlCoordinates

class CameraOutputSurfaceRender(context: Context) : BaseFboRenderer(
    context,
    "shader/camera/marker/vertex.glsl",
    "shader/camera/marker/fragment.glsl"
) {
    override fun onDraw(
        width: Int,
        height: Int,
        texture: Int,
        frameBuffer: Int,
        chain: FboRendererChain
    ) {
        setVertexAttributeLocation("a_vertexCoors", vertexCoordinateBuffer)
        setVertexAttributeLocation("a_textureCoors", textureCoordinateBuffer)
        bindTextureToSample("u_texture", GLES20.GL_TEXTURE_2D, texture)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 , 4)
    }

    override fun createVertexCoordinates() = GlCoordinates.createNormalVertexPositions()

    override fun createTextureCoordinates() = GlCoordinates.createNormalTexturePositions()
}