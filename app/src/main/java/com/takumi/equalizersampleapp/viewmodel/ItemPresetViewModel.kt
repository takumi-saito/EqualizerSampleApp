package com.takumi.equalizersampleapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.takumi.equalizersampleapp.data.Event
import com.takumi.equalizersampleapp.data.Preset
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemPresetViewModel @AssistedInject constructor(
    @Assisted preset: Preset,
) : ViewModel() {

    private val _preset = MutableLiveData(preset)
    val preset: LiveData<Preset> = _preset

    private val _eventPreset = MutableLiveData<Event<Preset>>()
    val eventPreset: LiveData<Event<Preset>> = _eventPreset

    private val _selected = MutableLiveData(false)
    val selected: LiveData<Boolean> = _selected

    fun notifySelected(selected: Boolean) {
        _selected.value = selected
    }

    fun onClick() {
        _preset.value?.let {
            _selected.value = true
            _eventPreset.value = Event(it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            preset: Preset,
        ): ItemPresetViewModel
    }

    companion object {
        fun provideFactory(
            preset: Preset,
            factory: Factory
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return factory.create(preset) as T
                }
            }
        }
    }
}