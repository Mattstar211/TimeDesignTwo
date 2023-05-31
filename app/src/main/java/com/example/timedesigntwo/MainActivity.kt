package com.example.timedesigntwo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.timedesigntwo.ui.login.AuthActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btnMenu: Button
    private lateinit var tasks: MutableList<Task>
    private lateinit var tasksComplete: MutableList<Task>
    private lateinit var taskList: ListView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var setDateCalendar: Date

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnMenu = findViewById(R.id.menuBtn)
        btnMenu.setOnClickListener {
            openMenu()
        }
        val sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE)
        val tasksJson = sharedPreferences.getString("tasks", null)
        val tasksCompleteJson = sharedPreferences.getString("tasksComplete", null)
        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            setDateCalendar = selectedDate.time
            filterTasksByDate(selectedDate)
        }

        if (tasksJson != null) {
            // Преобразуем строку JSON в список задач
            val tasksType = object : TypeToken<List<Task>>() {}.type
            tasks = Gson().fromJson(tasksJson, tasksType)
        } else {
            tasks = mutableListOf()
        }
        if (tasksCompleteJson != null) {
            val tasksCompleteType = object : TypeToken<List<Task>>() {}.type
            tasksComplete = Gson().fromJson(tasksCompleteJson, tasksCompleteType)
        } else {
            tasksComplete = mutableListOf()
        }

        taskList = findViewById(R.id.taskList)
        taskAdapter = TaskAdapter(this, tasks)
        taskList.adapter = taskAdapter

        val updatedTasks = intent.getSerializableExtra("tasks") as? ArrayList<Task>
        if (updatedTasks != null) {
            updateTaskList(updatedTasks)
        }


        taskList.setOnItemLongClickListener { parent, view, position, id ->
            openMenuSet(tasks[position], view)
            true
        }

        taskList.setOnItemClickListener { parent, view, position, id ->
            openEditActivity(tasks[position])
            true
        }

    }

    private fun filterTasksByDate(selectedDate: Calendar) {
        val filteredTasks = tasks.filter { task ->
            val taskDate = task.date
            taskDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                    taskDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                    taskDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
        }
        taskAdapter = TaskAdapter(this, filteredTasks)
        taskList.adapter = taskAdapter

        taskList.setOnItemLongClickListener { parent, view, position, id ->
            openMenuSet(filteredTasks[position], view)
            true
        }

        taskList.setOnItemClickListener { parent, view, position, id ->
            openEditActivity(filteredTasks[position])
            true
        }
    }

    fun authUsers() {
        val authIntent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

    private fun openEditActivity(task: Task) {
        val taskIntent = Intent(this, EditTaskActivity::class.java)
        taskIntent.putExtra("task", task)
        taskIntent.putExtra("tasks", ArrayList(tasks))
        startActivity(taskIntent)
    }

    private fun updateTaskList(updatedTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(updatedTasks)
        taskAdapter.notifyDataSetChanged()
        val sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tasksJson = Gson().toJson(tasks)
        editor.putString("tasks", tasksJson)
        editor.apply()
    }

    private fun openMenu() {
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        saveTasks()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_task -> {
                    val tasksIntent = Intent(this, TasksActivity::class.java)
                    startActivity(tasksIntent)
                    true
                }
                R.id.menu_complete -> {
                    val tasksIntent = Intent(this, CompleteTasksActivity::class.java)
                    startActivity(tasksIntent)
                    true
                }
                R.id.menu_analitics -> {
                    val tasksIntent = Intent(this, AnaliticsActivity::class.java)
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

    private fun openMenuSet(task: Task, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.task_context_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_del -> {
                    tasks.remove(task)
                    taskAdapter.notifyDataSetChanged()
                    saveTasks()
                    true
                }
                R.id.menu_complete -> {
                    task.duration = System.currentTimeMillis() - task.startTime
                    tasksComplete.add(task)
                    tasks.remove(task)
                    Log.d("duration", task.duration.toString())
                    saveTasks()
                    true
                }
                R.id.menu_start -> {
                    task.startTime = System.currentTimeMillis()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tasksJson = Gson().toJson(tasks)
        val tasksCompleteJson = Gson().toJson(tasksComplete)
        editor.putString("tasks", tasksJson)
        editor.putString("tasksComplete", tasksCompleteJson)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Преобразуем список задач в строку JSON
        val tasksJson = Gson().toJson(tasks)
        val tasksCompleteJson = Gson().toJson(tasksComplete)
        // Сохраняем строку JSON в SharedPreferences
        editor.putString("tasks", tasksJson)
        editor.putString("tasksComplete", tasksCompleteJson)
        editor.apply()
    }
}
