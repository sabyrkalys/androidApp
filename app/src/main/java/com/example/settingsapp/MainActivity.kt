package com.example.settingsapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val allItems = listOf(
        SettingsItem("Wi-Fi", "Подключение к сети", "📶", "#4C6EF5", Settings.ACTION_WIFI_SETTINGS, isSectionStart = true),
        SettingsItem("NearLink", "Быстрое соединение устройств", "📡", "#0EA5A4", Settings.ACTION_WIRELESS_SETTINGS),

        SettingsItem("Экран", "Яркость, тема, шрифт", "🖥️", "#8B5CF6", Settings.ACTION_DISPLAY_SETTINGS, isSectionStart = true),
        SettingsItem("Звук", "Громкость, вибрация", "🔊", "#A855F7", Settings.ACTION_SOUND_SETTINGS),
        SettingsItem("Уведомления", "Настройка уведомлений приложений", "🔔", "#D946EF", "android.settings.APP_NOTIFICATION_SETTINGS"),

        SettingsItem("Батарея", "Заряд и энергосбережение", "🔋", "#EAB308", Intent.ACTION_POWER_USAGE_SUMMARY, isSectionStart = true),
        SettingsItem("Дата и время", "Часовой пояс, формат времени", "🕒", "#F43F5E", Settings.ACTION_DATE_SETTINGS),
        SettingsItem("Язык и ввод", "Язык системы, клавиатура", "🌐", "#EC4899", Settings.ACTION_LOCALE_SETTINGS)
    )

    private lateinit var adapter: SettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = SettingsAdapter(allItems) { item ->
            if (item.title == "NearLink") {
                Toast.makeText(this, R.string.nearlink_hint, Toast.LENGTH_LONG).show()
            }
            openSettings(item.action)
        }
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter

        findViewById<android.widget.TextView>(R.id.controlCenterButton).setOnClickListener {
            startActivity(Intent(this, ControlCenterActivity::class.java))
        }

        findViewById<EditText>(R.id.searchInput).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(query: String) {
        val filtered = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter { it.title.contains(query, ignoreCase = true) }
        }
        adapter.updateItems(filtered)
    }

    private fun openSettings(action: String) {
        try {
            startActivity(Intent(action))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.unavailable_hint, Toast.LENGTH_SHORT).show()
        }
    }
}
