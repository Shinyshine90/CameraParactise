package cn.shawn.camerapractise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.shawn.camerapractise.camera1.CameraApi1Helper
import cn.shawn.camerapractise.databinding.ActivityMainBinding

private const val TAG = "MainActivityTAG"

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val cameraApi1 by lazy {
        CameraApi1Helper(viewBinding.surfaceView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.btnShot.setOnClickListener {

        }
    }

}