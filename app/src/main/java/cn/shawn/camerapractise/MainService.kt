package cn.shawn.camerapractise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.core.util.CarcorderLog

private const val TAG = "MainService"

class MainService : Service() {

    override fun onCreate() {
        super.onCreate()
        CarcorderLog.d(TAG, "onCreate")
    }

    override fun onBind(intent: Intent): IBinder {
        CarcorderLog.d(TAG, "onBind")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground()
        }
        return Binder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        CarcorderLog.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CarcorderLog.d(TAG, "onStartCommand")

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        CarcorderLog.d(TAG, "onDestroy")
    }

    private fun startForeground() {
        val channel = "KeepLive"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, channel)
        builder.setContentTitle("")
            .setContentText("")
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
        val notification = builder.build()
        startForeground(0x1024, notification)
    }

}