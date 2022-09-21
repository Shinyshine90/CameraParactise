package com.example.core.opengl.render

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.example.core.entity.MatrixTransform
import com.example.core.entity.RenderMode
import com.example.core.opengl.fbo.BaseFboRenderer
import com.example.core.opengl.fbo.FboRendererChain
import com.example.core.util.GlCoordinates

class CameraRgbaModeRender(context: Context) : BaseFboRenderer(
    context,
    "shader/camera/rgba/vertex.glsl",
    "shader/camera/rgba/fragment.glsl"
) {

    override fun createVertexCoordinates() = GlCoordinates.createNormalVertexPositions()

    override fun createTextureCoordinates() = GlCoordinates.createNormalTexturePositions()

    override fun onDraw(width: Int, height: Int, texture: Int, frameBuffer: Int, chain: FboRendererChain) {
        clearSurface()
        setVertexAttributeLocation("a_vertexCoors", vertexCoordinateBuffer)
        setVertexAttributeLocation("a_textureCoors", textureCoordinateBuffer)

        val transform = chain.getTag(MatrixTransform::class.java).transform
        val locationTransform = GLES20.glGetUniformLocation(glProgram, "u_transformMatrix")
        GLES20.glUniformMatrix4fv(locationTransform, 1, false, transform, 0)

        val renderMode = chain.getTag(RenderMode::class.java).mode
        val locationRenderMode = GLES20.glGetUniformLocation(glProgram, "u_renderMode")
        GLES20.glUniform1i(locationRenderMode, renderMode)

        bindTextureToSample("u_texture", GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 ,4)
    }



}