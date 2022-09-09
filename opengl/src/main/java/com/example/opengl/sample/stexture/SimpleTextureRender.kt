package com.example.opengl.sample.stexture

import android.content.Context
import android.opengl.GLES20
import com.example.opengl.base.render.BaseGlRender
import com.example.opengl.util.AssetsUtils
import com.example.opengl.util.BitmapUtils
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.toFloatBuffer
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SimpleTextureRender(context: Context): BaseGlRender(context) {

    private val vertexShaderCode = AssetsUtils.loadString(context, "shader/stexture/vertex.glsl") ?: ""

    private val fragmentShaderCode = AssetsUtils.loadString(context, "shader/stexture/fragment.glsl") ?: ""

    private var glProgram = 0

    private val vertexPosition =
        floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f, -1f, -1f).toFloatBuffer()

    private val texturePosition =
        floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 1f).toFloatBuffer()

    private val bitmap by lazy {
        BitmapUtils.loadFromAssets(context, "texture/jin.webp")
    }
    private fun loadTextureBuffer(): Buffer {
        val buffer = ByteBuffer.allocateDirect(bitmap.width * bitmap.height * 4 )
            .order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(buffer)
        bitmap.recycle()
        buffer.position(0)
        return buffer
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        glProgram = GLES20.glCreateProgram()
        val vertexShaderId = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShaderId = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        GLES20.glAttachShader(glProgram, vertexShaderId)
        GLES20.glAttachShader(glProgram, fragmentShaderId)
        GLES20.glLinkProgram(glProgram)

        GLES20.glUseProgram(glProgram)

        val aPositionLocation = GLES20.glGetAttribLocation(glProgram, "a_position")
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, vertexPosition)

        val aTexturePositionLocation = GLES20.glGetAttribLocation(glProgram, "a_texturePosition")
        GLES20.glEnableVertexAttribArray(aTexturePositionLocation)
        GLES20.glVertexAttribPointer(aTexturePositionLocation, 2, GLES20.GL_FLOAT, false, 0, texturePosition)

        //纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        val imageTexture = textures[0]

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            bitmap.width, bitmap.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, loadTextureBuffer())

        val textureLocation = GLES20.glGetUniformLocation(glProgram, "u_texture")
        GLES20.glUniform1i(textureLocation, 2)

    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
    }
}