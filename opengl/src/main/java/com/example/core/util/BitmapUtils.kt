package com.example.core.util

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.jvm.Throws

object BitmapUtils {

    fun handleMirrorRotate(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(180f)
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }

    @Throws(IOException::class)
    fun compressToDisk(file: File, bitmap: Bitmap) {
        val out = FileOutputStream(file)
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
            out.flush()
            out.close()
        }
    }
}