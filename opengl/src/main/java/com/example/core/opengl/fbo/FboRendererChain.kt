package com.example.core.opengl.fbo

open class FboRendererChain(private val renderers: List<BaseFboRenderer>) {

    private val tags: MutableMap<Class<*>, in Any> by lazy {
        mutableMapOf()
    }

    fun <T: Any> tag(value: T) {
        tags[value.javaClass] = value
    }

    fun <T> getTag(clz: Class<T>) : T {
        @Suppress("UNCHECKED_CAST")
        return tags[clz] as? T ?: throw IllegalStateException()
    }

    fun initRender() {
        renderers.forEach { renderer ->
            renderer.initGlProgram()
        }
    }

    fun processRender(width: Int, height: Int, texture: Int, index: Int) {
        if (renderers.size <= index) return
        val render = renderers[index]
        val targetBuffer = if (index == renderers.size - 1) {
            0
        } else {
            render.frameBuffer
        }
        render.setSurfaceSize(width, height)
        render.draw(width, height, texture, targetBuffer, index, this)
    }
}