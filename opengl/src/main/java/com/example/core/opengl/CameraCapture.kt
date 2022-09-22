package com.example.core.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Looper
import android.os.SystemClock
import android.view.Surface
import com.example.core.capture.PhotoCapture
import com.example.core.entity.MatrixTransform
import com.example.core.entity.RenderMode
import com.example.core.entity.config.CaptureConfig
import com.example.core.opengl.egl.EglEnvironment
import com.example.core.opengl.render.CameraRenderChain
import com.example.core.util.CarcorderLog
import com.example.core.util.GlUtils
import com.example.core.view.PreviewSurface

class CameraCapture(
    private val context: Context,
    private val captureConfig: CaptureConfig,
    private val photoCapture: PhotoCapture,
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
        runOnGlThread {
            eglEnvironment.init()
        }
    }

    private fun initOpenGlProgram() {
        runOnGlThread {
            //支持透明纹理
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            cameraRenderChain.initRender()
        }
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
            drawSurface(transformMatrix)
            surfaceTexture.updateTexImage()
        }
    }

    private fun drawSurface(transformMatrix:FloatArray) {
        runOnGlThread {
            fun EGLSurface.drawTexture() {
                val startStamp = SystemClock.elapsedRealtime()
                eglEnvironment.makeCurrentSurface(this)
                cameraRenderChain.tag(MatrixTransform(transformMatrix))
                cameraRenderChain.tag(fetchRenderMode())
                cameraRenderChain.processRender(
                    captureConfig.width, captureConfig.height, oesTexture, 0)
                handlePhotoCapture(cameraRenderChain.getOutputFrameBufferTexture())
                eglEnvironment.eglSwapBuffers(this)
                CarcorderLog.d("CameraCapture", "draw cost ${SystemClock.elapsedRealtime() - startStamp}")
            }
            previewEglSurface?.drawTexture()
            recorderEglSurface?.drawTexture()
        }
    }

    private fun handlePhotoCapture(texture: Int) {
        val request = photoCapture.peek() ?: return
        if (texture < 0) {
            photoCapture.handleCapture(request, Result.failure(RuntimeException("illegal oesTexture")))
        } else {
            val stamp = SystemClock.elapsedRealtime()
            val bitmap = GlUtils.saveTexture(texture, captureConfig.width, captureConfig.height)
            CarcorderLog.d("PhotoCapture", "cost ${SystemClock.elapsedRealtime() - stamp}")
            photoCapture.handleCapture(request, Result.success(bitmap))
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
        initEgl()
        //初始化OPENGL
        initOpenGlProgram()
    }

}