package cn.shawn.camerademo

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraDevice
import android.opengl.EGL14
import android.opengl.EGLSurface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import com.smartlink.foundation.camera.CamerasManager
import com.smartlink.foundation.camera.session.PreviewCaptureSession
import com.smartlink.foundation.opengl.OpenGlThread
import com.smartlink.foundation.opengl.egl.EglEnvironment
import com.smartlink.foundation.opengl.entity.SurfaceSize
import com.smartlink.foundation.opengl.entity.TransformMatrix
import com.smartlink.foundation.opengl.fbo.FboRendererChain
import com.smartlink.foundation.opengl.utils.GLUtils
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private val eglEnvironment by lazy { EglEnvironment() }

    private val openGlThread by lazy { OpenGlThread() }

    private val camerasManager by lazy { CamerasManager(this) }

    private val renderChain by lazy { FboRendererChain(listOf(CameraRgbaModeRender(this))) }

    private var oesSurfaceTexture: SurfaceTexture? = null

    private var oesTexture = -1

    private var videoRecorder: VideoRecorder? = null

    private var previewEglSurface: EGLSurface? = null

    private var recordEglSurface: EGLSurface? = null

    private val displaySurfaceSize = SurfaceSize.DisplaySurfaceSize()

    private val frameBufferSize = SurfaceSize.FrameBufferSize()

    private val transformMatrix = TransformMatrix(FloatArray(16))

    private var surfaceWidth = 0

    private var surfaceHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepareEglOpenGl()
        openCamera()
        val button = findViewById<Button>(R.id.btn)
        button.setOnClickListener {
            if (videoRecorder == null) {
                startRecord()
            } else {
                stopRecord()
            }
            button.text = if (videoRecorder == null) "start" else "stop"
        }
        val surfaceView = findViewById<View>(R.id.sv)
        surfaceView.viewTreeObserver.addOnGlobalLayoutListener {
            surfaceWidth = surfaceView.width
            surfaceHeight = surfaceView.height
        }
    }

    override fun onResume() {
        super.onResume()
        startPreview()
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }

    private fun openCamera() {
        val cameraId = camerasManager.getCameraId(CamerasManager.LensFacing.BACK)
            ?: throw RuntimeException()
        camerasManager.openCamera(cameraId) {
            it.fold({ cameraDevice ->
                startCapture(cameraDevice)
            }) { error ->
                throw RuntimeException(error.message)
            }
        }
    }

    private fun prepareEglOpenGl() {
        runOnGlThread {
            eglEnvironment.init()
            renderChain.initRender()
        }
    }

    private fun startCapture(cameraDevice: CameraDevice) {
        runOnGlThread {
            oesTexture = GLUtils.createOESTexture()
            oesSurfaceTexture = SurfaceTexture(oesTexture).apply {
                setDefaultBufferSize(1280, 720)
                setOnFrameAvailableListener(this@MainActivity::onFrameAvailable)
                camerasManager.startCapture(PreviewCaptureSession(cameraDevice, Surface(this)))
            }
        }
    }

    private fun startPreview() {
        fun createEglSurface(surface: Surface) {
            runOnUiThread {
                eglEnvironment.makeCurrentSurface(EGL14.EGL_NO_SURFACE)
                previewEglSurface = eglEnvironment.createEGLSurface(surface)
            }
        }
        val surfaceView = findViewById<SurfaceView>(R.id.sv)
        if (surfaceView.holder.surface.isValid) {
            createEglSurface(surfaceView.holder.surface)
        } else {
            surfaceView.holder.addCallback(object: SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    createEglSurface(holder.surface)
                }
                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                override fun surfaceDestroyed(holder: SurfaceHolder) {}
            })
        }
    }

    private fun stopPreview() {
        previewEglSurface?.apply {
            runOnGlThread {
                eglEnvironment.destroyEglSurface(this)
            }
        }
        previewEglSurface = null
    }

    private fun startRecord() {
        stopRecord()
        videoRecorder = VideoRecorder(this, VideoRecorder.VideoConfig(1280, 720),
            getVideoTakePath(this)).apply {
            prepare()
            runOnGlThread {
                this.recordSurface?.apply {
                    eglEnvironment.makeCurrentSurface(EGL14.EGL_NO_SURFACE)
                    recordEglSurface = eglEnvironment.createEGLSurface(this)
                }
            }
            start()
        }
    }

    private fun stopRecord() {
        val recorder = videoRecorder ?: return
        recorder.stop()
        recorder.release()
        videoRecorder = null
    }

    private fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        fun EGLSurface.onRenderDraw() {
            eglEnvironment.makeCurrentSurface(this)
            displaySurfaceSize.resize(surfaceWidth, surfaceHeight)
            frameBufferSize.resize(surfaceWidth, surfaceHeight)
            renderChain.setTransformMatrix(transformMatrix)
            renderChain.setDisplaySize(displaySurfaceSize)
            renderChain.setFrameBufferSize(frameBufferSize)
            renderChain.processRender(oesTexture, 0)
            eglEnvironment.eglSwapBuffers(this)
        }
        runOnGlThread {
            surfaceTexture.getTransformMatrix(transformMatrix.transform)
            recordEglSurface?.onRenderDraw()
            previewEglSurface?.onRenderDraw()
            surfaceTexture.updateTexImage()
        }
    }

    private fun runOnGlThread(task: () -> Unit) {
        openGlThread.execute(task)
    }

    private val counter = AtomicInteger(0)

    private fun getVideoTakePath(context: Context): String {
        return File(context.getExternalFilesDir("VideoCapture"),
            "${counter.getAndIncrement()}.mp4").absolutePath
    }

}