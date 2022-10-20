package com.smartlink.foundation.opengl.entity

data class TransformMatrix(val transform: FloatArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransformMatrix

        if (!transform.contentEquals(other.transform)) return false

        return true
    }

    override fun hashCode(): Int {
        return transform.contentHashCode()
    }
}