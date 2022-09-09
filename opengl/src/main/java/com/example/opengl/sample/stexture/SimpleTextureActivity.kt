package com.example.opengl.sample.stexture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opengl.databinding.ActivitySimpleTextureBinding

class SimpleTextureActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySimpleTextureBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.root.setRenderer(SimpleTextureRender(this))

    }
}