package com.example.core.util

import android.util.Log

private const val TAG = "CarcorderCore"

object CarcorderLog {
    
    fun d(tag: String, msg: String) {
        Log.d(TAG, "$tag: $msg")
    }
    
    fun e(tag: String, msg: String) {
        Log.e(TAG, "$tag: $msg")
    }
}