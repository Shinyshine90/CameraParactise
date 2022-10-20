package cn.shawn.camerademo

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.smartlink.foundation.opengl.fbo.BaseFboRenderer
import com.smartlink.foundation.opengl.fbo.FboRendererChain
import com.smartlink.foundation.opengl.utils.GLCoordinateFactory

class CameraRgbaModeRender(context: Context) : BaseFboRenderer(
    context,
    "shader/camera/rgba/vertex.glsl",
    "shader/camera/rgba/fragment.glsl"
) {

    override fun createVertexCoordinates() = GLCoordinateFactory.createNormalVertexPositions()

    override fun createTextureCoordinates() = GLCoordinateFactory.createNormalTexturePositions()

    override fun onDraw(texture: Int, frameBuffer: Int, chain: FboRendererChain) {
        clearSurface()
        val frameBufferSize = chain.getFrameBufferSize()
        GLES20.glViewport(0, 0, frameBufferSize.width, frameBufferSize.height)

        setVertexAttributeLocation("a_vertexCoors", vertexCoordinateBuffer)
        setVertexAttributeLocation("a_textureCoors", textureCoordinateBuffer)

        val locationTransformMatrix = GLES20.glGetUniformLocation(glProgram, "u_transformMatrix")
        GLES20.glUniformMatrix4fv(locationTransformMatrix, 1, false, chain.getTransformMatrix().transform, 0)

        bindTextureToSample("u_texture", GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 ,4)
    }

}