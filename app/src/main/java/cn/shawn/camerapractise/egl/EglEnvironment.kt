package cn.shawn.camerapractise.egl

import android.graphics.SurfaceTexture
import android.opengl.*
import android.view.Surface
/**
 * EGL环境配置模版
 * 在子线程中调用@init()初始化EGL环境，即可完成环境配置
 */
class EglEnvironment {

    lateinit var eglDisplay: EGLDisplay

    lateinit var eglConfig: EGLConfig

    lateinit var eglContext: EGLContext

    fun init() {
        eglDisplay = createEglDisplay()
        //初始化EGL环境
        if (!EGL14.eglInitialize(eglDisplay, null, 0, null, 0)) {
            throw RuntimeException("egl init failed!")
        }

        //选择EGLConfig
        eglConfig = chooseEglConfig(eglDisplay)
        //创建EGL Context
        eglContext = createEglContext(eglDisplay, eglConfig)
        //创建一个假的EglSurfaceView，否则会影响EGL的初始化流程
        val fakeEglSurface = createEGLSurface(Surface(SurfaceTexture(-1)))
        //
        makeCurrentSurface(fakeEglSurface)
    }

    private fun createEglDisplay(): EGLDisplay {
        val eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (eglDisplay == null || eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("get egl display error!")
        }
        return eglDisplay
    }

    private fun chooseEglConfig(display: EGLDisplay): EGLConfig {
        val attribList = intArrayOf(
            EGL14.EGL_BUFFER_SIZE, 32,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_NONE
        )
        val configs: Array<EGLConfig?> = arrayOfNulls(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(
                display,
                attribList,
                0,
                configs,
                0,
                configs.size,
                numConfigs,
                0
            )
        ) {
            throw RuntimeException("eglChooseConfig failed")
        }
        return configs[0] ?: throw RuntimeException("eglChooseConfig error")
    }

    private fun createEglContext(display: EGLDisplay, config: EGLConfig): EGLContext {
        val contextList = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        val context = EGL14.eglCreateContext(
            display,
            config,
            EGL14.EGL_NO_CONTEXT,
            contextList,
            0
        )
        if (context == null || context == EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("createEglContext failed")
        }
        return context
    }

    /**
     * 创建Egl Surface
     */
    fun createEGLSurface(surface: Surface): EGLSurface {
        val attribList = intArrayOf(
            EGL14.EGL_NONE
        )
        return EGL14.eglCreateWindowSurface(
            eglDisplay,
            eglConfig,
            surface,
            attribList,
            0
        ) ?: throw RuntimeException("createEglSurface failed")
    }

    /**
     * 指定OpenGl绘制到eglSurface上
     */
    fun makeCurrentSurface(eglSurface: EGLSurface) {
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
    }

}