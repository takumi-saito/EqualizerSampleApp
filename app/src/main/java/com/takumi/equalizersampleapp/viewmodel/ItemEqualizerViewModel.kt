package com.takumi.equalizersampleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import com.takumi.equalizersampleapp.data.Event
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemEqualizerViewModel @AssistedInject constructor(
    @Assisted("max") val max: Short,
    @Assisted("min") val min: Short,
    @Assisted("band") val band: Short,
    @Assisted("initBandLevel") val initBandLevel: Short,
    @Assisted("centerFreq") val centerFreq: Int
) : ViewModel() {

    val seekMax = MutableLiveData<Int>(max - min)
    val seekProgress = MutableLiveData<Int>(initBandLevel - min)
    val seekProgressPercent = MediatorLiveData<Int>().also {
        it.addSource(seekProgress) { seekProgress ->
            it.value = ((seekProgress + min) / 100) * 100
        }
    }.distinctUntilChanged()

    private val _stopTrackingTouchProgress = MutableLiveData<Event<Int>>()
    val stopTrackingTouchProgress: LiveData<Event<Int>> = _stopTrackingTouchProgress

    private val _seekDisplay = MutableLiveData<String>("${(centerFreq / 1000)}Hz")
    val seekDisplay: LiveData<String> = _seekDisplay

    fun notifyUpdateProgress(bandLevel: Short) {
        val diff = bandLevel - min
        seekProgress.value = diff
    }

    fun notifyUpdateSeekProgress(progress: Int) {
        seekProgress.value = progress
    }
    fun notifyUpdateStopTrackingTouchSeekBar(progress: Int) {
        _stopTrackingTouchProgress.value = Event((((progress + min) / 100) * 100))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("max") max: Short,
            @Assisted("min") min: Short,
            @Assisted("band") band: Short,
            @Assisted("initBandLevel") initBandLevel: Short,
            @Assisted("centerFreq") centerFreq: Int
        ): ItemEqualizerViewModel
    }

    companion object {
        fun provideFactory(
            max: Short,
            min: Short,
            band: Short,
            initBandLevel: Short,
            centerFreq: Int,
            factory: Factory
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return factory.create(max, min, band, initBandLevel, centerFreq) as T
                }
            }
        }
    }
}