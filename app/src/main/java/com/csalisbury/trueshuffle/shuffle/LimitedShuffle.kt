package com.csalisbury.trueshuffle.shuffle

import javax.inject.Inject

class LimitedShuffle @Inject constructor() : Shuffle() {
    override val name = "Limited Shuffle"
    override val sortOrder = 2
}