package com.example.timedesigntwo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

// Модель данных для задачи
data class Task(
    var descriptionName: String,
    var date: Calendar,
    var description: String,
    var startTime: Long = 0L,
    var duration: Long = 0L
) : Serializable

// Адаптер для списка задач
class TaskAdapter(context: Context, private val tasks: List<Task>) :
    ArrayAdapter<Task>(context, 0, tasks) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            // Инфлейт макета элемента списка задач
            view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        }

        val task = tasks[position]

        val descriptionTextView = view?.findViewById<TextView>(R.id.descriptionTextView)
        val dateTextView = view?.findViewById<TextView>(R.id.dateTextView)
        val timeTextView = view?.findViewById<TextView>(R.id.durationTextView)
        // Установка данных задачи в элементы макета
        descriptionTextView?.text = task.descriptionName
        dateTextView?.text = formatDate(task.date)
        Log.d("timeDuration", convertTimeToString(task.duration))
        if (task.duration != 0L) timeTextView?.text = "Время выполения: "+ convertTimeToString(task.duration)
        else if (task.startTime != 0L) timeTextView?.text =
            "Время выполения: " + convertTimeToString(System.currentTimeMillis() - task.startTime)
        else timeTextView?.text = "00:00:00"
        return view!!
    }

    private fun formatDate(date: Calendar): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date.time)

    }

    fun convertTimeToString(timeMillis: Long): String {
        val hours = (timeMillis / (1000 * 60 * 60)) % 24
        val minutes = (timeMillis / (1000 * 60)) % 60
        val seconds = (timeMillis / 1000) % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

class TasksActivity : AppCompatActivity() {
    private lateinit var taskText: EditText
    private lateinit var taskDescriptionText: EditText
    private lateinit var addButton: Button
    private lateinit var taskList: ListView
    private lateinit var tasks: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private var selectedDate: Calendar? = null
    private lateinit var dateButton: Button
    private lateinit var btnMenu: Button
    private var CHANNEL_ID: String = "CHANNEL_ID"
    private val channelName = "My Channel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                // Уведомления отключены, запросите разрешение на отображение уведомлений
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        }

        // Инициализация элементов пользовательского интерфейса
        taskText = findViewById(R.id.taskText)
        taskDescriptionText = findViewById(R.id.taskDescription)
        addButton = findViewById(R.id.addButton)
        dateButton = findViewById(R.id.dateButton)
        taskList = findViewById(R.id.taskList)
        btnMenu = findViewById(R.id.menuBtn)

        // Инициализация списка задач и адаптера
        val sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE)
        val tasksJson = sharedPreferences.getString("tasks", null)
        if (tasksJson != null) {
            // Преобразуем строку JSON в список задач
            val tasksType = object : TypeToken<List<Task>>() {}.type
            tasks = Gson().fromJson(tasksJson, tasksType)
        } else {
            tasks = mutableListOf()
        }
        taskList = findViewById(R.id.taskList)
        taskAdapter = TaskAdapter(this, tasks)
        taskList.adapter = taskAdapter
        // Обработчики событий кнопок
        addButton.setOnClickListener {
            addTask()
            saveTasks()
        }
        dateButton.setOnClickListener(View.OnClickListener { showDatePickerDialog() })
        btnMenu.setOnClickListener {
            openMenu()
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
    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tasksJson = Gson().toJson(tasks)
        editor.putString("tasks", tasksJson)
        editor.apply()
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
                R.id.menu_analitics -> {
                    val mainIntent = Intent(this, AnaliticsActivity::class.java)
                    startActivity(mainIntent)
                    true
                }
                R.id.menu_complete -> {
                    val mainIntent = Intent(this, CompleteTasksActivity::class.java)
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
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun openEditActivity(task: Task) {
        val taskIntent = Intent(this, EditTaskActivity::class.java)
        taskIntent.putExtra("task", task)
        taskIntent.putExtra("tasks", ArrayList(tasks))
        startActivity(taskIntent)
    }

    // Добавление задачи
    private fun addTask() {
        val taskDescriptionName = taskText.text.toString()
        val taskDescription = taskDescriptionText.text.toString()
        if (taskDescriptionName.isNotEmpty() && selectedDate != null) {
            // Создание новой задачи и добавление её в список
            val task = Task(taskDescriptionName, selectedDate!!, taskDescription)
            tasks.add(task)
            taskAdapter.notifyDataSetChanged()
            taskText.setText("")
            taskDescriptionText.setText("")
            selectedDate = null
            startAlarm(task)
        }
    }

    private fun startAlarm(task: Task) {
        val alertCalendar = task.date.clone() as Calendar
        alertCalendar.set(Calendar.HOUR_OF_DAY, 8)
        alertCalendar.set(Calendar.MINUTE, 0)
        alertCalendar.set(Calendar.SECOND, 0)
        alertCalendar.set(Calendar.DAY_OF_MONTH, task.date.get(Calendar.DAY_OF_MONTH) - 1)

        val alertCalendar1 = task.date.clone() as Calendar
        alertCalendar1.set(Calendar.HOUR_OF_DAY, 18)
        alertCalendar1.set(Calendar.MINUTE, 0)
        alertCalendar1.set(Calendar.SECOND, 0)
        alertCalendar1.set(Calendar.DAY_OF_MONTH, task.date.get(Calendar.DAY_OF_MONTH) - 1)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlertReceiver::class.java)
        intent.putExtra("descriptionName", task.descriptionName)
        intent.putExtra("description", task.description)

        val requestCode1 = System.currentTimeMillis().toInt()
        val requestCode2 = System.currentTimeMillis().toInt() + 1

        val pendingIntent1 = PendingIntent.getBroadcast(
            this,
            requestCode1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pendingIntent2 = PendingIntent.getBroadcast(
            this,
            requestCode2,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertCalendar.timeInMillis, pendingIntent1)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertCalendar1.timeInMillis, pendingIntent2)
    }

    // Отображение диалога выбора даты
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val time = calendar.get(Calendar.HOUR_OF_DAY)
        Log.d("TimeCalendar", time.toString())
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate = Calendar.getInstance()
                selectedDate?.set(year, monthOfYear, dayOfMonth)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}