package cn.shawn.camerapractise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.children
import cn.shawn.camerapractise.databinding.ActivityMainBinding
import com.example.core.CarcorderManager
import com.example.core.entity.RenderMode

private const val TAG = "MainActivityTAG"

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        handleClick()
    }

    override fun onStart() {
        super.onStart()
        CarcorderManager.startPreview(viewBinding.sv)
    }

    override fun onStop() {
        super.onStop()
        CarcorderManager.stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun handleClick() {
        findViewById<ViewGroup>(R.id.ll).children.forEach {
            it.setOnClickListener {
                val renderMode = when (it.id) {
                    R.id.btn_l -> RenderMode.SINGLE_SIDE_LEFT
                    R.id.btn_r -> RenderMode.SINGLE_SIDE_RIGHT
                    R.id.btn_f -> RenderMode.SINGLE_SIDE_FRONT
                    R.id.btn_b -> RenderMode.SINGLE_SIDE_BACK

                    R.id.btn_lr -> RenderMode.DOUBLE_SIDE_LR_FAIRLY
                    R.id.btn_lwr -> RenderMode.DOUBLE_SIDE_LR_LEFT_WEIGHT
                    R.id.btn_lrw -> RenderMode.DOUBLE_SIDE_LR_RIGHT_WEIGHT

                    R.id.btn_lrb -> RenderMode.TRIPLE_SIDE_LRB

                    R.id.btn_t -> RenderMode.FOUR_SIDE_T
                    R.id.btn_h -> RenderMode.FOUR_SIDE_H
                    else -> RenderMode.FOUR_SIDE_T
                }
                CarcorderManager.setRenderMode(renderMode)
            }
        }
    }
}