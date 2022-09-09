package com.example.opengl.shape

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.opengl.util.BitmapUtils
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.scaleXYMatrix
import com.example.opengl.util.ext.toFloatBuffer
import com.example.opengl.util.ext.translateXYMatrix
import java.nio.ByteBuffer

class SimpleTexture(private val context: Context) {

    private val bitmap by lazy {
        BitmapUtils.loadFromAssets(context, "texture/jin.webp")
    }

    private val textureBuffer by lazy {
        val buffer = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(buffer)
        buffer.position(0)
        buffer
    }

    private val vertexPosition =
        floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f, -1f, -1f).toFloatBuffer()

    private val texturePosition =
        floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 1f).toFloatBuffer()

    private val glProgram = GLES20.glCreateProgram()

    init {
        GLES20.glAttachShader(
            glProgram,
            ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, SHADER_VERTEX)
        )
        GLES20.glAttachShader(
            glProgram,
            ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, SHADER_FRAGMENT)
        )
        GLES20.glLinkProgram(glProgram)
        GLES20.glUseProgram(glProgram)

        val locationVertexPosition = GLES20.glGetAttribLocation(glProgram, "a_Position")
        GLES20.glEnableVertexAttribArray(locationVertexPosition)
        GLES20.glVertexAttribPointer(
            locationVertexPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexPosition
        )

        val locationTexturePosition = GLES20.glGetAttribLocation(glProgram, "a_TexturePosition")
        GLES20.glEnableVertexAttribArray(locationTexturePosition)
        GLES20.glVertexAttribPointer(
            locationTexturePosition,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            texturePosition
        )

        //处理纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        val imageTexture = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
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
            bitmap.width,
            bitmap.height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            textureBuffer
        )

        val locationTexture = GLES20.glGetUniformLocation(glProgram, "u_TextureId")
        GLES20.glUniform1i(locationTexture, 0)

    }

    fun draw(mode: RenderMode) {
        val transformMatrix = FloatArray(16)
        val scaleMatrix = scaleXYMatrix(mode.scaleX, mode.scaleY)
        val translateMatrix = translateXYMatrix(mode.translateX, mode.translateY)
        //先缩放，在位移
        Matrix.multiplyMM(transformMatrix, 0, scaleMatrix, 0, translateMatrix, 0)

        val locationTransform = GLES20.glGetUniformLocation(glProgram, "u_TransformMatrix");
        GLES20.glUniformMatrix4fv(locationTransform, 1, false , transformMatrix.toFloatBuffer())
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
    }

    companion object {
        const val SHADER_VERTEX =
            """
                precision mediump float;
                attribute vec4 a_Position;
                attribute vec2 a_TexturePosition;
                varying   vec2 v_TexturePosition;
                uniform   mat4 u_TransformMatrix;
                void main() {
                    v_TexturePosition = a_TexturePosition;
                    gl_Position = u_TransformMatrix * a_Position;
                }
            """

        const val SHADER_FRAGMENT =
            """
                precision mediump float;
                uniform sampler2D u_TextureId;
                varying  vec2 v_TexturePosition;
                void main() {
                    gl_FragColor = texture2D(u_TextureId, v_TexturePosition); 
                    //gl_FragColor = vec4(1.0, 0, 0, 1.0);
                
                }
            """
    }

}

enum class RenderMode(
    val translateX: Float,
    val translateY: Float,
    val scaleX:Float,
    val scaleY:Float,
    ) {
    FULL(0f, 0f, 1f, 1f),
    LT(0.5f, -0.5f, 2f, 2f),
    LB(0.5f, 0.5f, 2f, 2f),
    RT(-0.5f, -0.5f, 2f, 2f),
    RB(-0.5f, 0.5f, 2f, 2f)
}