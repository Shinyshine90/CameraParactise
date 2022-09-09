package com.example.opengl.shape

import android.opengl.GLES20
import com.example.opengl.util.ShaderUtils
import com.example.opengl.util.ext.rotateXYMatrix
import com.example.opengl.util.ext.toFloatBuffer

class Square {

    private val vertexBuffer = floatArrayOf(
        0F, 0F,
        0.5F, 0.5F,
        0.5F, -0.5F,
        1F, 0F
    ).toFloatBuffer()

    private val colorData = floatArrayOf(
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
    ).toFloatBuffer()

    private val translateVec = floatArrayOf(0.2f, 0.2f, 0f, 0f).toFloatBuffer()

    private val rotateMatrix = rotateXYMatrix(30f).toFloatBuffer()

    private val glProgram = GLES20.glCreateProgram()

    init {
        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(glProgram, vertexShader)
        GLES20.glAttachShader(glProgram, fragmentShader)
        GLES20.glLinkProgram(glProgram)
    }

    fun draw(scale: Float) {
        GLES20.glUseProgram(glProgram)
        //配置定点坐标系
        val vertexLocation = GLES20.glGetAttribLocation(glProgram, "a_Position")
        GLES20.glEnableVertexAttribArray(vertexLocation)
        GLES20.glVertexAttribPointer(vertexLocation, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        //缩放, float
        val scaleLocation = GLES20.glGetUniformLocation(glProgram, "u_Scale")
        GLES20.glUniform1f(scaleLocation, scale)
        //平移, vector
        val transVecLocation = GLES20.glGetUniformLocation(glProgram, "u_TranslateVec")
        GLES20.glUniform4fv(transVecLocation, 1, translateVec)
        //旋转, matrix
        val rotateLocation = GLES20.glGetUniformLocation(glProgram, "u_RotateMatrix")
        GLES20.glUniformMatrix4fv(rotateLocation, 1, false, rotateMatrix)
        //配置片元着色器颜色
        val fragColorLocation = GLES20.glGetAttribLocation(glProgram, "a_Color")
        GLES20.glEnableVertexAttribArray(fragColorLocation)
        GLES20.glVertexAttribPointer(fragColorLocation, 4, GLES20.GL_FLOAT, false, 0, colorData)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 4)
        GLES20.glDisableVertexAttribArray(vertexLocation)
    }

    companion object {
        const val VERTEX_SHADER =
            """
                precision mediump float;
                attribute vec4 a_Position;
                attribute vec4 a_Color;
                varying   vec4 v_Color;
                uniform float u_Scale;
                uniform vec4  u_TranslateVec;
                uniform mat4  u_RotateMatrix;
                void main() {
                    mat4 scales = mat4(
                        1, 0.0, 0.0, 0.0,
                        0.0, u_Scale, 0.0, 0.0,
                        0.0, 0.0, 1, 0.0,
                        0.0, 0.0, 0.0, 1
                    );
                    v_Color = a_Color;
                    gl_Position = scales * a_Position + u_TranslateVec;
                }
            """

        const val FRAGMENT_SHADER =
            """
                precision mediump float;
                varying   vec4 v_Color;
                void main() {
                    gl_FragColor = v_Color;
                }
            """
    }
}