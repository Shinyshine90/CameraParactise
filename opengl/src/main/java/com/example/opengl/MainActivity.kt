package com.example.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.opengl.databinding.ActivityMainBinding
import com.example.opengl.shape.RenderMode

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val changeMode = { v: View ->
            binding.surface.updateMode(when (v) {
                binding.tvLt -> RenderMode.LT
                binding.tvLb -> RenderMode.LB
                binding.tvRt -> RenderMode.RT
                binding.tvRb -> RenderMode.RB
                else -> RenderMode.FULL
            })
        }
        binding.tvLt.setOnClickListener(changeMode)
        binding.tvLb.setOnClickListener(changeMode)
        binding.tvRt.setOnClickListener(changeMode)
        binding.tvRb.setOnClickListener(changeMode)
    }
}