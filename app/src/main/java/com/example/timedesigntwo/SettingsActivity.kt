package com.example.timedesigntwo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var btnMenu: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        btnMenu = findViewById(R.id.menuBtn)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnMenu.setOnClickListener {
            openMenu()
        }
    }
    private fun openMenu() {
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_task -> {
                    val mainIntent = Intent(this, TasksActivity::class.java)
                    startActivity(mainIntent)
                    true
                }
                R.id.menu_calendar -> {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    true
                }
                R.id.menu_complete -> {
                    val tasksIntent = Intent(this, CompleteTasksActivity::class.java)
                    startActivity(tasksIntent)
                    true
                }
                R.id.menu_analitics -> {
                    val settingsIntent = Intent(this, AnaliticsActivity::class.java)
                    startActivity(settingsIntent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Обработчик изменения настройки темы
            val themePreference = findPreference<ListPreference>("theme_preference")
            themePreference?.setOnPreferenceChangeListener { preference, newValue ->
                if (preference is ListPreference && newValue is String) {
                    val selectedTheme = newValue.toString()

                    // Проверяем, не совпадает ли выбранная тема с текущей темой
                    if (selectedTheme != themePreference.value) {
                        // Применяем выбранную тему
                        when (selectedTheme) {
                            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }
                }
                true
            }

        }
    }


}