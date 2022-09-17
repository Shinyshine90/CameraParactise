package cn.shawn.camerapractise

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import android.view.Surface
import cn.shawn.camerapractise.egl.BaseGlRender

private const val TAG = "CameraEglRender"

class CameraEglRender(context: Context):
    BaseGlRender(context, "shader/camera_vertex.glsl", "shader/camera_normal_fragment.glsl") {

    private var oesTextureId = -1

    private var oesTexture: SurfaceTexture? = null

    private val transformMatrix= FloatArray(16)

    private lateinit var previewEglSurface: EGLSurface
    
    fun generateOESTexture(onCameraTextureCreated: (SurfaceTexture) -> Unit) {
        runOnGlThread {
            oesTextureId = createOESTexture()
            oesTexture  = SurfaceTexture(oesTextureId).apply {
                setDefaultBufferSize(1920, 1080)
                setOnFrameAvailableListener(this@CameraEglRender::onFrameAvailable)
                onCameraTextureCreated(this)
            }
            Log.d("CameraRender", "init generateOESTexture")
        }
    }

    fun setupPreviewSurface(surface: Surface) {
        runOnGlThread {
            previewEglSurface = eglEnvironment.createEGLSurface(surface)
            eglEnvironment.makeCurrentSurface(previewEglSurface)
        }
    }
    
    private fun onFrameAvailable(texture: SurfaceTexture) {
        Log.e(TAG, "onFrameAvailable: ${Thread.currentThread()}")
        runOnGlThread {
            texture.updateTexImage()
            onDraw()
        }
    }

    override fun onDraw() {
        super.onDraw()
        makeCurrentEglSurface(previewEglSurface)
        GLES20.glViewport(0, 0, 1080, 1920)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_SAMPLER_EXTERNAL_OES, oesTextureId)

        val textureLocation = GLES20.glGetUniformLocation(glProgramId, "u_texture")
        GLES20.glUniform1i(textureLocation, 0)

        oesTexture?.apply {
            getTransformMatrix(transformMatrix)
            val textureMatrixLocation = GLES20.glGetUniformLocation(glProgramId, "u_rotateMatrix")
            GLES20.glUniformMatrix4fv(textureMatrixLocation, 1, false, transformMatrix, 0)
        }

        Log.d(TAG, "onDraw: ${Thread.currentThread()}")
        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        EGL14.eglSwapBuffers(eglEnvironment.eglDisplay, previewEglSurface)
    }

    override fun getVertexPosition() = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)

    override fun getFragmentPosition() = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f)

    override fun getVertexPositionStride() = 2

    override fun getFragmentPositionStride() = 2

    override fun getVertexPositionName() = "a_vertexPosition"

    override fun getFragmentPositionName() = "a_texturePosition"

}