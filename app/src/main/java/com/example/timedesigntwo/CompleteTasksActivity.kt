package com.example.timedesigntwo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.ArrayList

class CompleteTasksActivity : AppCompatActivity() {
    private lateinit var btnMenu: Button
    private lateinit var tasksComplete: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: ListView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_tasks)
        btnMenu = findViewById(R.id.menuBtn)
        taskList = findViewById(R.id.taskList)

        btnMenu.setOnClickListener {
            openMenu()
        }
        val sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE)
        val tasksCompleteJson = sharedPreferences.getString("tasksComplete", null)
        if (tasksCompleteJson != null) {
            val tasksCompleteType = object : TypeToken<List<Task>>() {}.type
            tasksComplete = Gson().fromJson(tasksCompleteJson, tasksCompleteType)
        } else {
            tasksComplete = mutableListOf()
        }
        taskAdapter = TaskAdapter(this, tasksComplete)
        taskList.adapter = taskAdapter
        taskAdapter.notifyDataSetChanged()
        taskList.setOnItemLongClickListener { parent, view, position, id ->
            openMenuSet(tasksComplete[position], view)
            true
        }
    }
    private fun openMenuSet(task: Task, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.complete_menu_task, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_del -> {
                    tasksComplete.remove(task)
                    taskAdapter.notifyDataSetChanged()
                    val sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val tasksCompleteJson = Gson().toJson(tasksComplete)
                    editor.putString("tasksComplete", tasksCompleteJson)
                    editor.apply()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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
                R.id.menu_analitics -> {
                    val mainIntent = Intent(this, AnaliticsActivity::class.java)
                    startActivity(mainIntent)
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