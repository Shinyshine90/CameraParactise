package cn.shawn.camerapractise

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaCodecList
import android.os.Build
import android.os.IBinder
import android.util.Log
import cn.shawn.camerapractise.test.PhotoCaptureTest
import cn.shawn.camerapractise.test.VideoCaptureTest
import com.example.core.util.CarcorderLog
import com.example.core.CarcorderManager
import com.example.core.entity.config.CaptureConfig

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        CarcorderLog.d("AppTag", "Application Create")
        val intent = Intent(this, MainService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent,object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                CarcorderLog.d("AppTag", "onServiceConnected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                CarcorderLog.d("AppTag", "onServiceDisconnected")
            }
        }, Context.BIND_AUTO_CREATE)
        CarcorderManager.init(this, CaptureConfig(1280 , 720))
        PhotoCaptureTest(this).start(10000, 1000)
        VideoCaptureTest(this).start(10_000, 1000)
        printEncode()
    }

    private fun printEncode() {
        MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos.filter {
            it.isEncoder
        }.forEach {
            val soft = it.name.startsWith("OMX.google")
            Log.d("AppTag", "printEncode: ${if (soft) "软" else "硬"} ${it.name}")
        }
    }
}