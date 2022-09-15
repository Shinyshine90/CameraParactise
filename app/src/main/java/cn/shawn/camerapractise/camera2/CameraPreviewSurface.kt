package cn.shawn.camerapractise.camera2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log

private const val TAG = "CameraPreviewSurface"

class CameraPreviewSurface @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val render = CameraRender(context).apply {
        onTextureCreated = this@CameraPreviewSurface::onCreateOESTexture
    }

    var onTextureCreated: ((texture: SurfaceTexture) -> Unit)? = null

    var onTakePicture: ((Bitmap) -> Unit)? = null
        set(value) {
            render.onTakePicture = value
            field = value
        }

    init {
        setEGLContextClientVersion(2)
        setRenderer(render)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun changeMode(mode: CameraRender.PreviewMode) {
        render.previewMode = mode
    }

    private fun onCreateOESTexture(texture: SurfaceTexture) {
        onTextureCreated?.invoke(texture)
        texture.setOnFrameAvailableListener(this::onRefreshFrame)
    }

    private fun onRefreshFrame(texture: SurfaceTexture?) {
        Log.d(TAG, "onRefreshFrame: ")
        texture?: return
        requestRender()
    }

}