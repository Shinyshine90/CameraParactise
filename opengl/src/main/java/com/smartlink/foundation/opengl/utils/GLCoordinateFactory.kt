package com.smartlink.foundation.opengl.utils

object GLCoordinateFactory {

    //创建整个Surface的定点坐标
    fun createNormalVertexPositions() = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )

    //创建整个纹理的坐标，对应纹理坐标系
    fun createNormalTexturePositions() = floatArrayOf(
        0f, 0f,
        1f, 0f,
        0f, 1f,
        1f, 1f
    )

    //创建整个纹理的坐标，对应纹理坐标系，对于普通的2D纹理，需要和对应的定点坐标系成镜像关系
    fun createReversedTexturePositions() = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )
}