package com.takumi.equalizersampleapp.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.takumi.equalizersampleapp.data.BandData
import com.takumi.equalizersampleapp.data.Preset
import javax.inject.Inject

class EqualizerRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun saveSettingsPreset(preset: Preset) {
        sharedPreferences.edit {
            putBoolean(
                "is_custom", preset is Preset.Custom
            )
            putInt(
                "preset_num", preset.presetNum.toInt()
            )
            putString(
                "preset_name", preset.presetName
            )
            commit()
        }
    }
    fun getSettingsPreset(): Preset {
        val isCustom = sharedPreferences.getBoolean("is_custom", false)
        return if (isCustom) {
            Preset.Custom(
                presetNum = (sharedPreferences.getInt("preset_num", Preset.Custom.DEF_CUSTOM.presetNum.toInt())).toShort(),
                presetName = sharedPreferences.getString("preset_name", "") ?: Preset.Custom.DEF_CUSTOM.presetName
            )
        } else {
            Preset.Default(
                presetNum = (sharedPreferences.getInt("preset_num", Preset.Default.EMPTY.presetNum.toInt())).toShort(),
                presetName = sharedPreferences.getString("preset_name", "") ?: Preset.Default.EMPTY.presetName
            )
        }
    }

    fun saveCustomBandData(band: Short, centerFreq: Int, progress: Int) {
        sharedPreferences.edit {
            putInt(
                "center_freq_${band}", centerFreq
            )
            putInt(
                "band_level_${band}", progress
            )
            commit()
        }
    }

    fun getCustomBandData(band: Short): BandData {
        return BandData(
            band = band,
            centerFreq = sharedPreferences.getInt("center_freq_$band", 0),
            bandLevel = sharedPreferences.getInt("band_level_$band", 0).toShort(),
        )
    }
}