package com.kltn.anigan.domain.enums

enum class ResolutionOption(val value: String) {
    _256x256("256x256"),
    _512x512("512x512"),
    _1024x1024("1024x1024");

    companion object {
        // Optional: Function to get an enum by its string value
        fun fromValue(value: String): ResolutionOption? {
            return entries.find { it.value == value }
        }

        fun toString(value: String): String {
            return when (value) {
                _256x256.value -> "Small"
                _512x512.value -> "Medium"
                _1024x1024.value -> "Large"
                else -> "Unsupported"
            }
        }
    }
}
