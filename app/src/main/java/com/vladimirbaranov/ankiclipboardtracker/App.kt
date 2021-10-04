package com.vladimirbaranov.ankiclipboardtracker

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ClipboardTrackerService.start(this)
    }
}