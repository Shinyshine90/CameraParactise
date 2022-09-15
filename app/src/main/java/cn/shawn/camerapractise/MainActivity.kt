package cn.shawn.camerapractise

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.shawn.camerapractise.camera2.CameraApi2Helper
import cn.shawn.camerapractise.camera2.CameraRender
import cn.shawn.camerapractise.databinding.ActivityMainBinding

private const val TAG = "MainActivityTAG"

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val camera2Api by lazy {
        CameraApi2Helper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.surfaceView.onTextureCreated = this::onTextureCreated
        viewBinding.surfaceView.onTakePicture = this::onReceivePicture
        viewBinding.btnNormal.setOnClickListener(this::changeMode)
        viewBinding.btnLeft.setOnClickListener(this::changeMode)
        viewBinding.btnRight.setOnClickListener(this::changeMode)
        viewBinding.btnTop.setOnClickListener(this::changeMode)
        viewBinding.btnBottom.setOnClickListener(this::changeMode)
        viewBinding.btnH.setOnClickListener(this::changeMode)
        camera2Api.printCameraInfo()
    }

    private fun onReceivePicture(bitmap: Bitmap) {
        viewBinding.ivPicture.post {
            viewBinding.ivPicture.setImageBitmap(bitmap)
        }
    }

    private fun changeMode(v: View) {
        val mode = when (v) {
            viewBinding.btnLeft -> CameraRender.PreviewMode.LEFT
            viewBinding.btnRight -> CameraRender.PreviewMode.RIGHT
            viewBinding.btnTop -> CameraRender.PreviewMode.TOP
            viewBinding.btnBottom -> CameraRender.PreviewMode.BOTTOM
            viewBinding.btnH -> CameraRender.PreviewMode.H
            else -> CameraRender.PreviewMode.NORMAL
        }
        viewBinding.surfaceView.changeMode(mode)
    }

    private fun onTextureCreated(texture: SurfaceTexture) {
        camera2Api.startPreview(texture)
    }


}