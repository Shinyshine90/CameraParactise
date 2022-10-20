package com.smartlink.foundation.opengl.utils

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import java.nio.Buffer
import java.nio.ByteBuffer

object GLUtils {

    /**
     * @param type Type GLES20.GL_VERTEX_SHADER/ GL_FRAGMENT_SHADER
     * @param shaderCode Shader code
     * @return
     */
    fun loadShader(program: Int, type: Int, shaderCode: String) {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // 检查编译是否出现异常
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("create shaderType $type failed : $log")
        }
        GLES20.glAttachShader(program, shader)
    }

    fun linkProgram(program: Int) {
        GLES20.glLinkProgram(program)
        //检查程序链接状态
        val linked = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("link program failed : $log")
        }
    }

    fun setVertexAttributeCoors(program: Int, name: String, buffer: Buffer) {
        buffer.position(0)
        val location = GLES20.glGetAttribLocation(program, name)
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, 2, GLES20.GL_FLOAT, false, 0, buffer)
    }

    fun setUniformMatrix(program: Int, name: String, matrix: FloatArray) {
        val location = GLES20.glGetUniformLocation(program, name)
        GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0)
    }

    fun createOESTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        //设置纹理过滤参数
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return textures[0]
    }

    fun createImageTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        //设置纹理过滤参数
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )
        //解除纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textures[0]
    }

    fun loadBitmapToImageTexture(texture: Int, bitmap: Bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(b)
        b.position(0)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            bitmap.width, bitmap.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    fun createFrameBuffer(width: Int, height: Int): Pair<Int, Int> {
        // 创建frame buffer绑定的纹理
        // Create texture which binds to frame buffer
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        val frameBufferTexture = textures[0]

        // 创建frame buffer
        // Create frame buffer
        val frameBuffers = IntArray(1)
        GLES20.glGenFramebuffers(frameBuffers.size, frameBuffers, 0)
        val frameBuffer = frameBuffers[0]

        // 将frame buffer与texture绑定
        // Bind the texture to frame buffer
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            frameBufferTexture,
            0
        )
        return Pair(frameBuffer, frameBufferTexture)
    }

    fun saveTexture(texture: Int, width: Int, height: Int): Bitmap {
        val frame = IntArray(1)
        GLES20.glGenFramebuffers(1, frame, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0
        )
        val buffer: ByteBuffer = ByteBuffer.allocate(width * height * 4)
        GLES20.glReadPixels(
            0, 0, width, height, GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE, buffer
        )
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glDeleteFramebuffers(1, frame, 0)
        return bitmap
    }

    private fun checkGlError(op: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            throw java.lang.RuntimeException("$op: glError $error")
        }
    }
}