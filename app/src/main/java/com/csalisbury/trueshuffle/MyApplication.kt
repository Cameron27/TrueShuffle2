package com.csalisbury.trueshuffle

import android.app.Application
import com.csalisbury.trueshuffle.di.ApplicationComponent
import com.csalisbury.trueshuffle.di.DaggerApplicationComponent

class MyApplication : Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}