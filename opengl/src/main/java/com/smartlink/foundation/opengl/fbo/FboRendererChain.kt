package com.smartlink.foundation.opengl.fbo

import com.smartlink.foundation.opengl.entity.SurfaceSize
import com.smartlink.foundation.opengl.entity.TransformMatrix

/**
 * Chain of Responsibility.
 * The scheduler of the content renderer, we divide the OpenGL operation into each individual Renderer,
 * and schedule those Renderers in the array data structure to render in a pipeline mode.
 */
open class FboRendererChain(private val renderers: List<BaseFboRenderer>) {

    private val tags: MutableMap<Class<*>, in Any> by lazy {
        mutableMapOf()
    }

    fun initRender() {
        renderers.forEach { renderer ->
            renderer.initGlProgram()
        }
    }

    /**
     * schedule the renderer to process render
     * @param displayWidth output display surface width
     * @param displayHeight output display surface height
     * @param frameBufferWidth display surface width
     * @param frameBufferHeight display surface height
     * @param texture input texture(OES/RGBA)
     * @param index index that target renderer in the chain
     */
    fun processRender(texture: Int, index: Int) {
        if (renderers.size <= index) return
        val render = renderers[index]
        val targetBuffer = if (index == renderers.size - 1) {
            0
        } else {
            render.frameBuffer
        }
        val frameBufferSize = getFrameBufferSize()
        render.createFrameBuffer(frameBufferSize.width, frameBufferSize.height)
        render.draw(texture, targetBuffer, index, this)
    }

    fun setDisplaySize(size: SurfaceSize.DisplaySurfaceSize) = tag(size)

    fun getDisplaySize(): SurfaceSize.DisplaySurfaceSize =
        getTag(SurfaceSize.DisplaySurfaceSize::class.java)

    fun setFrameBufferSize(size: SurfaceSize.FrameBufferSize) = tag(size)

    fun getFrameBufferSize(): SurfaceSize.FrameBufferSize =
        getTag(SurfaceSize.FrameBufferSize::class.java)

    fun setTransformMatrix(transformMatrix: TransformMatrix) = tag(transformMatrix)

    fun getTransformMatrix() = getTag(TransformMatrix::class.java)

    fun <T: Any> tag(value: T) {
        tags[value.javaClass] = value
    }

    fun <T> getTag(clz: Class<T>) : T {
        @Suppress("UNCHECKED_CAST")
        return tags[clz] as? T ?: throw IllegalStateException()
    }

    fun getOutputFrameBufferTexture(): Int =
        renderers.getOrNull(renderers.size - 2)?.frameBufferTexture ?: -1
}