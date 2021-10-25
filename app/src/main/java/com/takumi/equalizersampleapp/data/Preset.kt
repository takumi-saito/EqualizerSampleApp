package com.takumi.equalizersampleapp.data

sealed class Preset {
    abstract val presetNum: Short
    abstract val presetName: String

    data class Default(
        override val presetNum: Short,
        override val presetName: String
    ) : Preset() {
        companion object {
            val EMPTY = Default(-1, "")
        }
    }
    data class Custom(
        override val presetNum: Short,
        override val presetName: String
    ) : Preset() {
        companion object {
            val DEF_CUSTOM = Custom(100, "custom")
        }
    }
}
