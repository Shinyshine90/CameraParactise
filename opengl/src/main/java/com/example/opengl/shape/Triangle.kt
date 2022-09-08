package com.example.opengl.shape

import android.opengl.GLES20
import com.example.opengl.shader.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle {

    private var vertexBuffer: FloatBuffer? = null

    // number of coordinates per vertex in this array
    val COORDS_PER_VERTEX = 2

    var triangleCoords = floatArrayOf(
        // in counterclockwise order:
        0.0f, 0.5f,  // top
        -0.5f, -0.5f,  // bottom left
        0.5f, -0.5f, // bottom right
    )

    // Set color with red, green, blue and alpha (opacity) values
    var color = floatArrayOf(255f, 0f, 0f, 1.0f)

    var glProgram = GLES20.glCreateProgram()


    init {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        val bb: ByteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        // 数组排列用nativeOrder
        bb.order(ByteOrder.nativeOrder())
        // 从ByteBuffer创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer()
        // 将坐标添加到FloatBuffer
        vertexBuffer?.put(triangleCoords)
        // 设置缓冲区来读取第一个坐标
        vertexBuffer?.position(0)

        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)

        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        GLES20.glAttachShader(glProgram, vertexShader)

        GLES20.glAttachShader(glProgram, fragmentShader)

        GLES20.glLinkProgram(glProgram)
    }

    fun draw() {
        GLES20.glUseProgram(glProgram)

        val vertexHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")

        GLES20.glEnableVertexAttribArray(vertexHandle)

        GLES20.glVertexAttribPointer(
            vertexHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        val scaleHandle = GLES20.glGetUniformLocation(glProgram, "u_scale")

        //GLES20.glUniformMatrix4fv(scaleHandle, 1, false, transformMatrix,0)

        //val translateHandle = GLES20.glGetUniformLocation(glProgram, "u_translate")

        //GLES20.glUniform2f(translateHandle, 0.5f, 0.5f)

        val colorHandler = GLES20.glGetUniformLocation(glProgram, "vColor")

        GLES20.glUniform4fv(colorHandler, 1, color, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)

        GLES20.glDisableVertexAttribArray(vertexHandle)

    }

    companion object {
        const val VERTEX_SHADER =
            """
                precision mediump float;
                uniform mat4 u_scale;
                uniform vec2 u_translate;
                attribute vec4 vPosition;
                void main() {
                    mat4 transMat = mat4(
                        1.0, 0.0, 0.0, 0.0,
                        0.0, 1.0, 0.0, 0.0,
                        0.0, 0.0, 1.0, 0.0,
                        u_translate.x, u_translate.y, 0.0, 1.0
                    );
                    gl_Position = transMat * u_scale * vPosition;
                }
            """

        const val FRAGMENT_SHADER =
            """
                precision mediump float;
                uniform vec4 vColor;
                void main() {
                    gl_FragColor = vColor;
                }
            """
    }
}