package com.example.opengl.util

import android.opengl.GLES20

object ShaderUtils {

    /**
     * @param type Type GLES20.GL_VERTEX_SHADER/ GL_FRAGMENT_SHADER
     * @param shaderCode Shader code
     * @return
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}