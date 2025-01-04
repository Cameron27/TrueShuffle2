package com.csalisbury.trueshuffle.di

import com.csalisbury.trueshuffle.LoginActivity
import com.csalisbury.trueshuffle.MainActivity
import com.csalisbury.trueshuffle.ShuffleActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ShuffleModule::class])
@Singleton
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: ShuffleActivity)
}