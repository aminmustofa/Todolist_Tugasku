package com.min.todolist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.min.todolist.data.Task
import com.min.todolist.databinding.ActivityAddEditTaskBinding
import com.min.todolist.util.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.*

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private var currentTask: Task? = null
    private var dueDate: Long = System.currentTimeMillis()
    private var startTime: Long = 0
    private var endTime: Long = 0

    companion object {
        const val EXTRA_TASK = "EXTRA_TASK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupCategoryDropdown()

        if (intent.hasExtra(EXTRA_TASK)) {
            @Suppress("DEPRECATION")
            currentTask = intent.getParcelableExtra(EXTRA_TASK)
            title = "Edit Tugas"
            populateUiWithTaskData()
        } else {
            title = "Tambah Tugas Baru"
            updateDateInView()
        }

        binding.textViewDueDate.setOnClickListener { showDatePicker() }
        binding.textViewStartTime.setOnClickListener { showTimePicker(true) }
        binding.textViewEndTime.setOnClickListener { showTimePicker(false) }
        binding.buttonSave.setOnClickListener { saveTask() }
    }

    private fun setupCategoryDropdown() {
        categoryViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryNames)
            binding.autoCompleteCategory.setAdapter(adapter)

            currentTask?.let {
                if (categoryNames.contains(it.category)) {
                    binding.autoCompleteCategory.setText(it.category, false)
                }
            }
        }
    }

    private fun populateUiWithTaskData() {
        currentTask?.let {
            binding.editTextTitle.setText(it.title)
            binding.editTextDescription.setText(it.description)
            dueDate = it.dueDate
            startTime = it.startTime
            endTime = it.endTime
            updateDateInView()
            updateTimeInView(binding.textViewStartTime, startTime)
            updateTimeInView(binding.textViewEndTime, endTime)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dueDate
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                dueDate = selectedCalendar.timeInMillis
                updateDateInView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val initialTime = if (isStartTime && startTime != 0L) startTime else if (!isStartTime && endTime != 0L) endTime else System.currentTimeMillis()
        calendar.timeInMillis = initialTime

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val timeCalendar = Calendar.getInstance()
            timeCalendar.set(Calendar.HOUR_OF_DAY, hour)
            timeCalendar.set(Calendar.MINUTE, minute)
            timeCalendar.set(Calendar.SECOND, 0)
            timeCalendar.set(Calendar.MILLISECOND, 0)

            if (isStartTime) {
                startTime = timeCalendar.timeInMillis
                updateTimeInView(binding.textViewStartTime, startTime)
            } else {
                endTime = timeCalendar.timeInMillis
                updateTimeInView(binding.textViewEndTime, endTime)
            }
        }
        TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.textViewDueDate.text = sdf.format(Date(dueDate))
    }

    private fun updateTimeInView(textView: TextView, timeInMillis: Long) {
        if (timeInMillis != 0L) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            textView.text = sdf.format(Date(timeInMillis))
        } else {
            if (textView.id == binding.textViewStartTime.id) {
                textView.text = "Waktu Mulai"
            } else {
                textView.text = "Waktu Selesai"
            }
        }
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val category = binding.autoCompleteCategory.text.toString()
        if (title.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Judul dan Kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }
        if (startTime != 0L && endTime != 0L && endTime <= startTime) {
            Toast.makeText(this, "Waktu selesai harus setelah waktu mulai", Toast.LENGTH_SHORT).show()
            return
        }

        val taskToSave = currentTask?.copy(
            title = title,
            description = description,
            dueDate = dueDate,
            startTime = startTime,
            endTime = endTime,
            category = category
        ) ?: Task(
            title = title,
            description = description,
            dueDate = dueDate,
            startTime = startTime,
            endTime = endTime,
            category = category
        )

        // Simpan atau perbarui tugas
        if (currentTask == null) {
            taskViewModel.insert(taskToSave)
        } else {
            taskViewModel.update(taskToSave)
        }

        // JADWALKAN NOTIFIKASI
        AlarmScheduler.schedule(this, taskToSave)

        setResult(Activity.RESULT_OK)
        finish()
    }
}