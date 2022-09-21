package com.example.core.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.example.core.entity.MatrixTransform
import com.example.core.entity.RenderMode
import com.example.core.entity.config.CaptureConfig
import com.example.core.opengl.egl.EglEnvironment
import com.example.core.opengl.render.CameraRenderChain
import com.example.core.util.GlUtils
import com.example.core.view.PreviewSurface

class CameraCapture(
    private val context: Context,
    private val captureConfig: CaptureConfig,
    private val fetchRenderMode: () -> RenderMode
) {
    private val openGlThread = OpenGlThread()

    private val eglEnvironment = EglEnvironment()

    private var oesSurfaceTexture: SurfaceTexture? = null

    private var previewSurface: PreviewSurface? = null

    private var previewEglSurface: EGLSurface? = null

    private var recorderEglSurface: EGLSurface? = null

    private var oesTexture = -1

    private val transformMatrix = FloatArray(16)

    private val cameraRenderChain by lazy {
        CameraRenderChain(context)
    }

    private fun initEgl() {
        eglEnvironment.init()
    }

    private fun initOpenGlProgram() {
        //支持透明纹理
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        cameraRenderChain.initRender()
    }

    fun setupPreviewSurface(surface: PreviewSurface) {
        removePreviewSurface()
        runOnGlThread {
            previewSurface = surface
            surface.setOnCreateCallback {
                previewEglSurface = eglEnvironment.createEGLSurface(it)
            }
        }
    }

    fun removePreviewSurface() {
        runOnGlThread {
            previewSurface?.release()
            previewSurface = null
            previewEglSurface?.apply {
                eglEnvironment.destroyEglSurface(this)
            }
            previewEglSurface = null
        }
    }

    fun setupRecorderSurface(surface: Surface) {
        runOnGlThread {
            recorderEglSurface = eglEnvironment.createEGLSurface(surface)
        }
    }

    fun createOESTexture(callback: (SurfaceTexture) -> Unit) {
        runOnGlThread {
            oesTexture = GlUtils.createOESTexture()
            val surfaceTexture = SurfaceTexture(oesTexture).apply{ oesSurfaceTexture = this}
            surfaceTexture.setDefaultBufferSize(captureConfig.width, captureConfig.height)
            surfaceTexture.setOnFrameAvailableListener(this@CameraCapture::onFrameAvailable)
            callback(surfaceTexture)
        }
    }

    private fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        runOnGlThread {
            surfaceTexture.getTransformMatrix(transformMatrix)
            draw(transformMatrix)
            surfaceTexture.updateTexImage()
        }
    }

    fun draw(transformMatrix:FloatArray) {
        runOnGlThread {
            fun EGLSurface.drawTexture() {
                eglEnvironment.makeCurrentSurface(this)
                cameraRenderChain.tag(MatrixTransform(transformMatrix))
                cameraRenderChain.tag(fetchRenderMode())
                cameraRenderChain.processRender(
                    captureConfig.width, captureConfig.height, oesTexture, 0)
                eglEnvironment.eglSwapBuffers(this)
            }
            previewEglSurface?.drawTexture()
            recorderEglSurface?.drawTexture()
        }
    }

    fun getGlHandler() = openGlThread.handler

    private fun runOnGlThread(executable: () -> Unit) {
        if (Looper.myLooper() == openGlThread.handler.looper) {
            executable()
        } else {
            openGlThread.execute(executable)
        }
    }

    init {
        //初始化EGL环境
        runOnGlThread {
            val stamp = SystemClock.elapsedRealtime()
            initEgl()
            initOpenGlProgram()
            Log.e("CameraCapture", "camera capture init cost: ${SystemClock.elapsedRealtime() - stamp}")
        }
    }

}