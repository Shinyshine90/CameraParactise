package com.example.core.util

import java.io.File

object DiskUtils {

    fun prepareFile(path: String): File {
        return File(path).apply {
            if (this.isFile) {
                this.deleteOnExit()
            }
            this.parentFile?.mkdirs()
        }
    }

}