package com.csalisbury.trueshuffle.di

import com.csalisbury.trueshuffle.shuffle.LimitedShuffle
import com.csalisbury.trueshuffle.shuffle.Shuffle
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
class ShuffleModule {
    @Provides
    @IntoSet
    fun provideFullShuffle(shuffle: Shuffle): Shuffle {
        return shuffle
    }

    @Provides
    @IntoSet
    fun provideLimitedShuffles(limitedShuffle: LimitedShuffle): Shuffle {
        return limitedShuffle
    }
}