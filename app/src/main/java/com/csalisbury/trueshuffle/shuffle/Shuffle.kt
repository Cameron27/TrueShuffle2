package com.csalisbury.trueshuffle.shuffle

import javax.inject.Inject

open class Shuffle @Inject constructor() {
    open val name: String = "Shuffle"
    open val sortOrder = 1
}