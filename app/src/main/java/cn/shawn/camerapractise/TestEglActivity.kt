package cn.shawn.camerapractise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import cn.shawn.camerapractise.camera2.CameraApi2Helper
import cn.shawn.camerapractise.databinding.ActivityTestEglBinding

private const val TAG = "TestEglActivity1"

class TestEglActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityTestEglBinding.inflate(layoutInflater)
    }

    private val cameraRender by lazy {
        CameraEglRender(this)
    }

    private val cameraHelper by lazy {
        CameraApi2Helper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.surface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                //cameraRender.setupPreviewSurface(holder.surface)
                Log.d("", "surfaceCreated: ")
                cameraRender.setupPreviewSurface(holder.surface)
                cameraRender.generateOESTexture {
                    cameraHelper.startPreview(it)
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.e(TAG, "surfaceChanged: ", )
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.e(TAG, "surfaceDestroyed: ", )
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }
}