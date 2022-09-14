package cn.shawn.camerapractise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.shawn.camerapractise.camera2.CameraApi2Helper
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

        camera2Api.printCameraInfo()


    }

}