package com.example.settingsapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = listOf(
            SettingsItem("Wi-Fi", Settings.ACTION_WIFI_SETTINGS),
            SettingsItem("Bluetooth", Settings.ACTION_BLUETOOTH_SETTINGS),
            SettingsItem("NearLink", "android.settings.NEARLINK_SETTINGS"),
            SettingsItem("Экран", Settings.ACTION_DISPLAY_SETTINGS),
            SettingsItem("Звук", Settings.ACTION_SOUND_SETTINGS),
            SettingsItem("Приложения", Settings.ACTION_APPLICATION_SETTINGS),
            SettingsItem("Хранилище", Settings.ACTION_INTERNAL_STORAGE_SETTINGS),
            SettingsItem("Батарея", Intent.ACTION_POWER_USAGE_SUMMARY),
            SettingsItem("Безопасность", Settings.ACTION_SECURITY_SETTINGS),
            SettingsItem("Локация", Settings.ACTION_LOCATION_SOURCE_SETTINGS),
            SettingsItem("Специальные возможности", Settings.ACTION_ACCESSIBILITY_SETTINGS),
            SettingsItem("Дата и время", Settings.ACTION_DATE_SETTINGS),
            SettingsItem("Язык и ввод", Settings.ACTION_LOCALE_SETTINGS),
            SettingsItem("Аккаунты", Settings.ACTION_SYNC_SETTINGS),
            SettingsItem("Уведомления", "android.settings.APP_NOTIFICATION_SETTINGS"),
            SettingsItem("NFC", "android.settings.NFC_SETTINGS"),
            SettingsItem("Режим полёта и сеть", Settings.ACTION_WIRELESS_SETTINGS),
            SettingsItem("О телефоне", Settings.ACTION_DEVICE_INFO_SETTINGS),
            SettingsItem("Все настройки", Settings.ACTION_SETTINGS)
        )

        findViewById<RecyclerView>(R.id.recyclerView).adapter = SettingsAdapter(items) { item ->
            openSettings(item.action)
        }
    }

    private fun openSettings(action: String) {
        try {
            startActivity(Intent(action))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Этот экран настроек недоступен на устройстве", Toast.LENGTH_SHORT).show()
        }
    }
}
