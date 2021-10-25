package com.takumi.equalizersampleapp.di

import android.content.Context
import android.content.SharedPreferences
import com.takumi.equalizersampleapp.data.repository.EqualizerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext applicationContext: Context,
    ): SharedPreferences {
        return applicationContext.getSharedPreferences("pref_equalizer.xml", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideEqualizerRepository(
        sharedPreferences: SharedPreferences
    ): EqualizerRepository {
        return EqualizerRepository(sharedPreferences)
    }
}