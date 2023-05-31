package com.example.timedesigntwo

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {
    private lateinit var btnMenu: Button
    private lateinit var tasks: MutableList<Task>
    private lateinit var taskDescription: EditText
    private lateinit var taskDate: CalendarView
    private lateinit var taskDescriptionName: EditText
    private lateinit var task: Task
    private lateinit var setDateTask: Calendar
    private lateinit var editBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        btnMenu = findViewById(R.id.menuBtn)
        taskDescription = findViewById(R.id.description)
        taskDate = findViewById(R.id.taskDate)
        taskDescriptionName = findViewById(R.id.descriptionName)
        editBtn = findViewById(R.id.editBtn)
        setDateTask = Calendar.getInstance()
        tasks = mutableListOf()
        tasks = intent.getSerializableExtra("tasks") as? ArrayList<Task> ?: mutableListOf()
        task = intent.getSerializableExtra("task") as Task

        taskDescriptionName.setText(task.descriptionName)
        taskDate.setDate(task.date.timeInMillis, true, true)
        taskDescription.setText(task.description)

        btnMenu.setOnClickListener() {
            openMenu()
            true
        }
        taskDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            setDateTask.set(year, month, dayOfMonth)
        }
        editBtn.setOnClickListener() {
            val position = tasks.indexOf(task)
            setText()
            var myToast: Toast
            if (position != -1) {
                // Обновить task в списке tasks
                tasks[position] = task
                myToast = Toast.makeText(this, "Успешное редактирование", Toast.LENGTH_SHORT)
                Log.d("tasks", "OK")
            } else {
                myToast = Toast.makeText(this, "Редактирование не удалось", Toast.LENGTH_SHORT)
                Log.d("tasks", "Not OK")
            }
            myToast.show()
            true
        }

    }

    private fun setText() {
        task.description = taskDescription.text.toString()

        // Создать новый объект Calendar и установить его значение
        val selectedDate = Calendar.getInstance()
        selectedDate.timeInMillis = setDateTask.timeInMillis

        task.date = selectedDate
        task.descriptionName = taskDescriptionName.text.toString()
        task.duration = 10000L
    }

    private fun openMenu() {
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.main_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_calendar -> {
                    // Переход к главной активности и передача списка задач
                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.putExtra("tasks", ArrayList(tasks))
                    startActivity(mainIntent)
                    true
                }
                R.id.menu_task -> {
                    // Переход к главной активности и передача списка задач
                    val taskIntent = Intent(this, TasksActivity::class.java)
                    taskIntent.putExtra("tasks", ArrayList(tasks))
                    startActivity(taskIntent)
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
}