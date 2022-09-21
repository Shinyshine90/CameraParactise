package cn.shawn.camerapractise

import android.app.Application
import com.example.core.CarcorderManager
import com.example.core.entity.config.CaptureConfig

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        CarcorderManager.init(this, CaptureConfig(1280, 720))
    }
}