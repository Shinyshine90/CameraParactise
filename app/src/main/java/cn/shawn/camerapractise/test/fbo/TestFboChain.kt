package cn.shawn.camerapractise.test.fbo

import android.content.Context
import com.example.core.opengl.fbo.FboRendererChain

class TestFboChain(context: Context): FboRendererChain(listOf(FboRenderer0(context), FboRenderer1(context)))