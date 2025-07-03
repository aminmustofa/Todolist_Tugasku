package com.min.todolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.min.todolist.R // Pastikan import R tidak abu-abu di file ini
import com.min.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Deklarasikan variabel binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi binding menggunakan layout inflater
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 2. Gunakan root dari binding sebagai content view
        setContentView(binding.root)

        // 3. Cari NavHostFragment menggunakan ID dari file R
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 4. Hubungkan BottomNavigationView (yang diakses melalui 'binding') dengan NavController
        binding.bottomNavView.setupWithNavController(navController)
    }
}