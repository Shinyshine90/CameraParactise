package com.example.core.entity

data class MatrixTransform(val transform: FloatArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatrixTransform

        if (!transform.contentEquals(other.transform)) return false

        return true
    }

    override fun hashCode(): Int {
        return transform.contentHashCode()
    }
}