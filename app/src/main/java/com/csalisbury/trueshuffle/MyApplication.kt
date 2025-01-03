package com.csalisbury.trueshuffle

import android.app.Application
import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
}

class MyApplication : Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}