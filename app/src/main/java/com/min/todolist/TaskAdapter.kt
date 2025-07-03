package com.min.todolist

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.min.todolist.data.Task
import com.min.todolist.databinding.ListItemTaskBinding

class TaskAdapter(
    private val onTaskClicked: (Task) -> Unit,
    private val onTaskCheckedChange: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        // PENTING: init block untuk memasang listener
        init {
            // Listener untuk klik pada seluruh item (untuk edit)
            binding.root.setOnClickListener {
                // Pastikan posisi valid untuk mencegah crash
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onTaskClicked(getItem(adapterPosition))
                }
            }

            // Listener untuk perubahan status checkbox
            binding.checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                // Pastikan posisi valid dan status benar-benar berubah
                // untuk mencegah loop tak terbatas saat bind ulang.
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Cek agar listener tidak terpanggil saat bind awal
                    if (binding.checkboxCompleted.isPressed) {
                        onTaskCheckedChange(getItem(adapterPosition), isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                textViewTitle.text = task.title
                textViewDueDate.text = task.formattedDate
                chipCategory.text = task.category

                // Set status checkbox tanpa memicu listener
                checkboxCompleted.isChecked = task.isCompleted

                if (task.formattedTime.isNotEmpty()) {
                    textViewTimeRange.visibility = View.VISIBLE
                    textViewTimeRange.text = task.formattedTime
                } else {
                    textViewTimeRange.visibility = View.GONE
                }

                if (task.isCompleted) {
                    textViewTitle.paintFlags = textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    root.alpha = 0.6f
                } else {
                    textViewTitle.paintFlags = textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    root.alpha = 1.0f
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
