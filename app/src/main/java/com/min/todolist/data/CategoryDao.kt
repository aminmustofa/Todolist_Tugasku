package com.min.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {

    /**
     * Menyisipkan satu kategori ke dalam tabel.
     * Fungsi ini ditangguhkan (suspend) agar bisa dipanggil dari coroutine.
     * @param category Objek kategori yang akan disisipkan.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    /**
     * Memperbarui kategori yang sudah ada.
     * Room menggunakan Primary Key untuk menemukan dan memperbarui baris yang sesuai.
     * @param category Objek kategori dengan data yang telah diperbarui.
     */
    @Update
    suspend fun update(category: Category)

    /**
     * Menghapus kategori dari tabel.
     * @param category Objek kategori yang akan dihapus.
     */
    @Delete
    suspend fun delete(category: Category)

    /**
     * Mengambil semua kategori dari tabel.
     * Daftar ini dibungkus dalam LiveData untuk observasi real-time dari UI.
     * Kategori diurutkan berdasarkan nama secara alfabetis (A-Z).
     * @return LiveData yang berisi daftar semua objek Category.
     */
    @Query("SELECT * FROM category_table ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>
}