package com.min.todolist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.min.todolist.data.Task
import com.min.todolist.databinding.FragmentHomeBinding
import com.min.todolist.util.AlarmScheduler
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var taskAdapter: TaskAdapter

    // Launcher untuk meminta izin notifikasi
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(
                requireView(),
                "Fitur notifikasi tidak akan aktif tanpa izin.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askNotificationPermission()
        setupRecyclerView()

        taskViewModel.upcomingTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.textViewUpcomingTaskCount.text = tasks.size.toString()
        }

        binding.fabAddTask.setOnClickListener {
            val intent = Intent(requireActivity(), AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                val intent = Intent(requireActivity(), AddEditTaskActivity::class.java)
                intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task)
                startActivity(intent)
            },
            onTaskCheckedChange = { task, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                taskViewModel.update(updatedTask)
                // Batalkan alarm jika tugas ditandai selesai
                if(isChecked) AlarmScheduler.cancel(requireContext(), updatedTask)
            }
        )
        binding.recyclerViewUpcoming.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
            attachSwipeToDelete(this, taskAdapter)
        }
    }

    private fun attachSwipeToDelete(recyclerView: RecyclerView, adapter: TaskAdapter) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskToDelete = adapter.currentList[viewHolder.adapterPosition]
                // Batalkan alarm sebelum menghapus dari DB
                AlarmScheduler.cancel(requireContext(), taskToDelete)
                taskViewModel.delete(taskToDelete)

                Snackbar.make(requireView(), "Tugas dihapus", Snackbar.LENGTH_LONG)
                    .setAction("Batal") {
                        taskViewModel.insert(taskToDelete)
                        // Jadwalkan kembali jika dibatalkan
                        AlarmScheduler.schedule(requireContext(), taskToDelete)
                    }
                    .show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun askNotificationPermission() {
        // Hanya untuk Android 13 (Tiramisu) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
