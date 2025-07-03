package com.min.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.min.todolist.data.Category
import com.min.todolist.databinding.DialogAddEditCategoryBinding
import com.min.todolist.databinding.FragmentCategoryBinding
import com.google.android.material.snackbar.Snackbar

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog(null) // Tambah kategori baru
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            showCategoryDialog(category) // Edit kategori saat diklik
        }

        binding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Terapkan swipe-to-delete di sini
            attachSwipeToDelete(this, categoryAdapter)
        }
    }

    private fun attachSwipeToDelete(recyclerView: RecyclerView, adapter: CategoryAdapter) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val categoryToDelete = adapter.currentList[viewHolder.adapterPosition]
                categoryViewModel.delete(categoryToDelete)

                Snackbar.make(requireView(), "Kategori '${categoryToDelete.name}' dihapus", Snackbar.LENGTH_LONG)
                    .setAction("Batal") {
                        categoryViewModel.insert(categoryToDelete)
                    }
                    .show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun showCategoryDialog(category: Category?) {
        val dialogBinding = DialogAddEditCategoryBinding.inflate(layoutInflater)
        val dialogTitle = if (category == null) "Tambah Kategori" else "Edit Kategori"

        category?.let {
            dialogBinding.editTextCategoryName.setText(it.name)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val categoryName = dialogBinding.editTextCategoryName.text.toString().trim()
                if (categoryName.isNotEmpty()) {
                    val newCategory = category?.copy(name = categoryName) ?: Category(name = categoryName)
                    if (category == null) {
                        categoryViewModel.insert(newCategory)
                    } else {
                        categoryViewModel.update(newCategory)
                    }
                } else {
                    Toast.makeText(requireContext(), "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
