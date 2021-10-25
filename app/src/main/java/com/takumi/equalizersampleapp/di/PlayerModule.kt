package com.takumi.equalizersampleapp.di

import android.content.Context
import android.media.audiofx.Equalizer
import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    fun buildMediaSource(
        @ApplicationContext applicationContext: Context
    ): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, "equalizer-sample-app")
        val uri = listOf<Uri>(
            Uri.parse("https://storage.googleapis.com/uamp/Kai_Engel_-_Irsens_Tale/01_-_Intro_udonthear.mp3"),
            Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        )
        val mediaSourceList = mutableListOf<MediaSource>().apply {
            uri.forEach {
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(it)
                add(mediaSource)
            }
        }
        return ConcatenatingMediaSource(*mediaSourceList.toTypedArray())
    }

    @Singleton
    @Provides
    fun provideSimpleExoPlayer(
        @ApplicationContext applicationContext: Context,
        mediaSource: MediaSource
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(applicationContext).build().apply {
            playWhenReady = playWhenReady
            prepare(mediaSource, false, false)
        }
    }

    @Provides
    fun provideEqualizer(
        simpleExoPlayer: SimpleExoPlayer
    ): Equalizer {
        val equalizer = Equalizer(
            0, // 優先度
            simpleExoPlayer.audioSessionId // セッションID
        )
        return equalizer
    }


}
