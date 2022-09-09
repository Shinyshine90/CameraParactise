package com.example.opengl.util

import android.content.Context
import java.io.BufferedReader

object AssetsUtils {

    fun loadString(context: Context, path: String): String? {
        return try {
            val `is` = context.assets.open(path)
            val bufferReader = BufferedReader(`is`.reader(Charsets.UTF_8))
            bufferReader.readText()
        } catch (e: Exception) {
            null
        }
    }
}