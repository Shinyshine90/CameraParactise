package cn.shawn.camerademo

import java.io.File

internal object DiskUtils {

    fun prepareFile(path: String): File {
        return File(path).apply {
            if (this.isFile) {
                this.deleteOnExit()
            }
            this.parentFile?.mkdirs()
        }
    }

}