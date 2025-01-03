package com.csalisbury.trueshuffle

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

class ShuffleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_shuffle)

        setupShuffleOptions()
    }

    private fun setupShuffleOptions() {
    }
}