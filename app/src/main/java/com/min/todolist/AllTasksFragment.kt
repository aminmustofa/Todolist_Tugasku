package com.min.todolist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.min.todolist.data.Task
import com.min.todolist.databinding.FragmentAllTasksBinding
import com.google.android.material.snackbar.Snackbar

class AllTasksFragment : Fragment() {

    private var _binding: FragmentAllTasksBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()

    private lateinit var upcomingTaskAdapter: TaskAdapter
    private lateinit var completedTaskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

        // TAMBAHKAN: Terapkan swipe-to-delete ke kedua RecyclerView
        attachSwipeToDelete(binding.recyclerViewUpcoming, upcomingTaskAdapter)
        attachSwipeToDelete(binding.recyclerViewCompleted, completedTaskAdapter)
    }

    private fun setupRecyclerViews() {
        val onTaskClicked: (Task) -> Unit = { task ->
            val intent = Intent(requireActivity(), AddEditTaskActivity::class.java)
            intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task)
            startActivity(intent)
        }
        val onTaskCheckedChange: (Task, Boolean) -> Unit = { task, isChecked ->
            taskViewModel.update(task.copy(isCompleted = isChecked))
        }

        upcomingTaskAdapter = TaskAdapter(onTaskClicked, onTaskCheckedChange)
        completedTaskAdapter = TaskAdapter(onTaskClicked, onTaskCheckedChange)

        binding.recyclerViewUpcoming.adapter = upcomingTaskAdapter
        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewCompleted.adapter = completedTaskAdapter
        binding.recyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        taskViewModel.upcomingTasks.observe(viewLifecycleOwner) { tasks ->
            upcomingTaskAdapter.submitList(tasks)
            binding.headerUpcoming.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
            checkIfEmpty()
        }

        taskViewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
            completedTaskAdapter.submitList(tasks)
            binding.headerCompleted.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
            checkIfEmpty()
        }
    }

    private fun checkIfEmpty() {
        val isUpcomingEmpty = taskViewModel.upcomingTasks.value.isNullOrEmpty()
        val isCompletedEmpty = taskViewModel.completedTasks.value.isNullOrEmpty()
        binding.textViewEmpty.visibility = if (isUpcomingEmpty && isCompletedEmpty) View.VISIBLE else View.GONE
    }

    /**
     * Fungsi untuk menambahkan fungsionalitas swipe-to-delete ke RecyclerView.
     */
    private fun attachSwipeToDelete(recyclerView: RecyclerView, adapter: TaskAdapter) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskToDelete = adapter.currentList[viewHolder.adapterPosition]
                taskViewModel.delete(taskToDelete)

                Snackbar.make(requireView(), "Tugas dihapus", Snackbar.LENGTH_LONG)
                    .setAction("Batal") {
                        taskViewModel.insert(taskToDelete)
                    }
                    .show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
