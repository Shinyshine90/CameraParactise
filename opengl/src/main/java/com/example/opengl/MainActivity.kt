package com.example.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.opengl.util.BitmapUtils

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BitmapUtils.loadFromAssets(this,"texture/baby.jpg").apply {
            Log.d(TAG, "onCreate: ${this.width}")
        }
    }
}