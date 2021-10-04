package com.vladimirbaranov.ankiclipboardtracker

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import android.content.pm.ResolveInfo





class ClipboardTrackerService : Service() {
    companion object {
        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"

        const val SERVICE_CHANNEL_ID: String = "ankitrackerservice"
        const val SERVICE_NOTIFY_ID = 1

        fun start(context: Context) {
            Intent(context, ClipboardTrackerService::class.java).also {
                it.action = ACTION_START
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                } else {
                    context.startService(it)
                }
            }
        }
    }

    private var isServiceStarted = false
    private var wakeLock: PowerManager.WakeLock? = null

    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var clipboard: ClipboardManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            when (action) {
                ACTION_START -> startService()
                ACTION_STOP -> stopService()
                else -> {
                }
            }
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notificationManagerCompat = NotificationManagerCompat.from(this)
        clipboard =
            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        startForeground(SERVICE_NOTIFY_ID, createNotification())
    }

    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
//        wakeLock =
//            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ClipboardTrackerService::lock").apply {
//                    acquire()
//                }
//            }
    }

    private fun stopService() {
//        try {
//            wakeLock?.let {
//                if (it.isHeld) {
//                    it.release()
//                }
//            }
//            stopForeground(true)
//            stopSelf()
//        } catch (e: Exception) {
//            //ignore
//        }
        isServiceStarted = false
    }



    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            with(channel) {
                description = getString(R.string.channel_description)
                enableVibration(false)
                enableLights(false)
                setSound(null, null)
                lockscreenVisibility = android.app.Notification.VISIBILITY_SECRET
            }
            notificationManagerCompat.createNotificationChannel(channel)

        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, AddWord::class.java),
            0
        )

        val notificationBuilder = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(false)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text))
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }
}