package com.example.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import kotlin.jvm.Throws

object AssetsUtils {

    /**
     * 以字符串的形式加载Assets中的文件
     */
    @Throws(IOException::class)
    fun loadString(context: Context, path: String): String {
        val `is` = context.assets.open(path)
        val bufferReader = BufferedReader(`is`.reader(Charsets.UTF_8))
        return bufferReader.readText()
    }

    /**
     * 以Bitmap的格式加载Assets中的资源
     */
    @Throws(IOException::class)
    fun loadBitmap(context: Context, path: String): Bitmap {
        val `is` = context.assets.open(path)
        return BitmapFactory.decodeStream(`is`)
    }

}