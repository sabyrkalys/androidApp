package com.example.settingsapp

data class SettingsItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val colorHex: String,
    val action: String,
    val isSectionStart: Boolean = false
)
