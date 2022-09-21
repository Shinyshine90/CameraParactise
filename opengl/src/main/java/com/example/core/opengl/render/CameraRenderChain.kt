package com.example.core.opengl.render

import android.content.Context
import com.example.core.opengl.fbo.FboRendererChain

class CameraRenderChain(context: Context):
    FboRendererChain(listOf(CameraRgbaModeRender(context), CameraWaterMarkRender(context)))