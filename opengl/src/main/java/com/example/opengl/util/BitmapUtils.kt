package com.example.opengl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object BitmapUtils {

    fun loadFromAssets(context: Context, path: String): Bitmap {
        val `is` = context.assets.open(path)
        return BitmapFactory.decodeStream(`is`)
    }
}