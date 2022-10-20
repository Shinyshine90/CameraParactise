package com.smartlink.foundation.opengl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import kotlin.jvm.Throws

object AssetsUtils {

    @Throws(IOException::class)
    fun loadString(context: Context, path: String): String {
        val `is` = context.assets.open(path)
        val bufferReader = BufferedReader(`is`.reader(Charsets.UTF_8))
        return bufferReader.readText()
    }

    @Throws(IOException::class)
    fun loadBitmap(context: Context, path: String): Bitmap {
        val `is` = context.assets.open(path)
        return BitmapFactory.decodeStream(`is`)
    }

}