package com.takumi.equalizersampleapp.viewmodel

import android.media.audiofx.Equalizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.takumi.equalizersampleapp.data.BandData
import com.takumi.equalizersampleapp.data.EqualizerData
import com.takumi.equalizersampleapp.data.Preset
import com.takumi.equalizersampleapp.data.repository.EqualizerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val equalizer: Equalizer,
    private val equalizerRepository: EqualizerRepository
) : ViewModel() {

    private val _equalizerData = MutableLiveData<EqualizerData>()
    val equalizerData: LiveData<EqualizerData> = _equalizerData

    private val _presetList = MutableLiveData<List<Preset>>(listOf())
    val presetList: LiveData<List<Preset>> = _presetList

    private val _currentPreset = MutableLiveData<Preset>()
    val currentPreset: LiveData<Preset> = _currentPreset

    init {
        setupEqualizer()
        equalizer.enabled = true
        setupPresetList()
        invokeSettingsCurrentPreset()
    }

    private fun setupEqualizer() {
        _equalizerData.value = EqualizerData(
            numberOfBands = equalizer.numberOfBands, // 調整を行うことのできる中心周波数がいくつあるか
            minEQLevel = equalizer.bandLevelRange[0], // 設定出来るlevelの下限
            maxEQLevel = equalizer.bandLevelRange[1], // 設定出来るlevelの上限
            bandDataList = (0 until equalizer.numberOfBands).map {
                val band = it.toShort()
                BandData(
                    band = band,
                    centerFreq = equalizer.getCenterFreq(band), // 周波数 単位はミリHz
                    bandLevel = equalizer.getBandLevel(band), // 周波数のlevel
                )
            }
        )
    }
    private fun setupPresetList() {
        val presets = equalizer.numberOfPresets
        val mutablePresetList = mutableListOf<Preset>(Preset.Custom.DEF_CUSTOM) // custom
        (0 until presets).forEach {
            mutablePresetList.add(
                Preset.Default(
                    it.toShort(),
                    equalizer.getPresetName(it.toShort())
                )
            )
        }
        _presetList.value = mutablePresetList
    }

    fun updateSettingsCurrentPreset(preset: Preset) {
        equalizerRepository.saveSettingsPreset(preset)
        _currentPreset.value = preset
    }

    private fun invokeSettingsCurrentPreset() {
        val preset = equalizerRepository.getSettingsPreset()
        if (preset.presetNum > 0) {
            _currentPreset.value = preset
        }
    }

    fun updateCustomBandData(band: Short, centerFreq: Int, progress: Int) {
        equalizerRepository.saveCustomBandData(band, centerFreq, progress)
    }

    fun notifyUpdateBandLevel(preset: Preset) {
        when (preset) {
            is Preset.Default -> {
                equalizer.usePreset(preset.presetNum)
                // バンドを取得
                setupEqualizer()
            }
            is Preset.Custom -> {
                val customEqualizerData = EqualizerData(
                    numberOfBands = equalizer.numberOfBands, // 調整を行うことのできる中心周波数がいくつあるか？
                    minEQLevel = equalizer.bandLevelRange[0],
                    maxEQLevel = equalizer.bandLevelRange[1],
                    bandDataList = (0 until equalizer.numberOfBands).map {
                        val band = it.toShort()
                        equalizerRepository.getCustomBandData(band)
                    }
                )
                _equalizerData.value = customEqualizerData
            }
        }
    }

    fun notifyUpdateBandLevel(band: Short, progress: Int) {
        equalizerData.value?.let {
            equalizer.setBandLevel(band, progress.toShort())
        }
    }
}