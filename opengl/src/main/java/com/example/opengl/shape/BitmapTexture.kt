package com.example.opengl.shape

import android.content.Context
import android.opengl.GLES20
import com.example.opengl.shader.scaleXYMatrix
import com.example.opengl.shader.toFloatBuffer
import com.example.opengl.shader.translateXYMatrix
import com.example.opengl.util.BitmapUtils
import com.example.opengl.util.ShaderUtils
import java.nio.ByteBuffer

class BitmapTexture(private val context: Context) {

    private val bitmap by lazy {
        BitmapUtils.loadFromAssets(context, "texture/baby.jpg")
    }

    private val textureBuffer by lazy {
        bitmap.run {
            val buffer = ByteBuffer.allocate(this.width * this.height * 4)
            this.copyPixelsToBuffer(buffer)
            buffer.position(0)
        }
    }

    private val vertexPositionBuffer = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f).toFloatBuffer()

    private val texturePositionBuffer = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f).toFloatBuffer()

    private val translateMatrix = translateXYMatrix(-0.5f, -0.5f).toFloatBuffer()

    private val scaleMatrix = scaleXYMatrix(2f, 2f).toFloatBuffer()

    private val glProgram = GLES20.glCreateProgram()

    init {
        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(glProgram, vertexShader)
        GLES20.glAttachShader(glProgram, fragmentShader)

        GLES20.glLinkProgram(glProgram)

        GLES20.glUseProgram(glProgram)


        val vertexPositionIndex = GLES20.glGetAttribLocation(glProgram, "a_Position")
        GLES20.glEnableVertexAttribArray(vertexPositionIndex)
        GLES20.glVertexAttribPointer(vertexPositionIndex, 2, GLES20.GL_FLOAT, false, 0, vertexPositionBuffer)


        val texturePositionIndex = GLES20.glGetAttribLocation(glProgram, "a_TexturePosition")
        GLES20.glEnableVertexAttribArray(texturePositionIndex)
        GLES20.glVertexAttribPointer(texturePositionIndex, 2, GLES20.GL_FLOAT, false, 0, texturePositionBuffer)

        //顶点平移
        val translateIndex = GLES20.glGetUniformLocation(glProgram, "u_TranslateMatrix")
        GLES20.glUniformMatrix4fv(translateIndex, 1, false, translateMatrix)

        //顶点平移
        val scaleIndex = GLES20.glGetUniformLocation(glProgram, "u_ScaleMatrix")
        GLES20.glUniformMatrix4fv(scaleIndex, 1, false, scaleMatrix)


        //创建纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        val imageTexture = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_NEAREST
        )
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

        //加载纹理
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.width, bitmap.height,
            0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, textureBuffer
        )

        val textureIndex = GLES20.glGetUniformLocation(glProgram, "u_Texture")
        GLES20.glUniform1i(textureIndex, 0)

    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,  0, 6)
    }

    companion object {
        const val VERTEX_SHADER =
            """
              precision mediump float;
              attribute vec4 a_Position;
              attribute vec2 a_TexturePosition;
              uniform mat4 u_TranslateMatrix;
              uniform mat4 u_ScaleMatrix;
              varying vec2 v_TexturePosition;
              void main() {
                v_TexturePosition = a_TexturePosition;
                gl_Position = u_ScaleMatrix * u_TranslateMatrix * a_Position;
              }
            """

        const val FRAGMENT_SHADER =
            """
                precision mediump float;
                uniform sampler2D u_Texture;
                varying vec2 v_TexturePosition;
                void main() {
                    gl_FragColor = texture2D(u_Texture, v_TexturePosition);
                }
            """
    }
}