package cn.shawn.camerapractise.test

import android.content.Context
import android.opengl.GLES20
import com.example.core.opengl.base.BaseShapeGlRenderer
import com.example.core.util.GlCoordinates

class SimpleGlRender(context: Context) :
    BaseShapeGlRenderer(context,
        "shader/simple/vertex.glsl", "shader/simple/fragment.glsl") {

    override fun createVertexCoordinates() = GlCoordinates.createNormalVertexPositions()

    override fun onDraw(width: Int, height: Int) {
        setVertexAttributeLocation("a_vertexCoors", vertexPositionBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3)
    }

}