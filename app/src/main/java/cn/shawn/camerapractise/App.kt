package cn.shawn.camerapractise

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import cn.shawn.camerapractise.test.CaptureTest
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
        //CaptureTest(this).start()
    }
}