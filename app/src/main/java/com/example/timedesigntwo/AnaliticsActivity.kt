package com.example.timedesigntwo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class AnaliticsActivity : AppCompatActivity() {
    private lateinit var btnMenu: Button
    private lateinit var tasks: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: ListView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analitics)
        btnMenu = findViewById(R.id.menuBtn)
        taskList = findViewById(R.id.taskList)


        val sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE)
        val tasksCompleteJson = sharedPreferences.getString("tasksComplete", null)
        if (tasksCompleteJson != null) {
            val tasksCompleteType = object : TypeToken<List<Task>>() {}.type
            tasks = Gson().fromJson(tasksCompleteJson, tasksCompleteType)
        } else {
            tasks = mutableListOf()
        }
        taskAdapter = TaskAdapter(this, tasks)
        taskList.adapter = taskAdapter
        taskAdapter.notifyDataSetChanged()

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
                R.id.menu_settings -> {
                    val settingsIntent = Intent(this, SettingsActivity::class.java)
                    startActivity(settingsIntent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}