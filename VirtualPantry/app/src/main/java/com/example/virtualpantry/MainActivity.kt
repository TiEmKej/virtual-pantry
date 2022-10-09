package com.example.virtualpantry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.virtualpantry.database.ApiService
import com.example.virtualpantry.fragments.FragmentAdd
import com.example.virtualpantry.fragments.FragmentEdit
import com.example.virtualpantry.fragments.FragmentPantry
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Fragmenty
        val fragmentPantry = FragmentPantry()
        val fragmentAdd = FragmentAdd()
        val fragmentEdit = FragmentEdit()

//        Ustawienie pierwszego ekranu
        replaceCurrentFragment(fragmentPantry)

//        Pasek nawigacyjny
        val navigationBarView = findViewById<BottomNavigationView>(R.id.bnvMenuBar)

        navigationBarView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.pantryMenu -> {
                    replaceCurrentFragment(fragmentPantry)
                    true
                }
                R.id.addMenu -> {
                    replaceCurrentFragment(fragmentAdd)
                    true
                }
                R.id.editMenu -> {
                    replaceCurrentFragment(fragmentEdit)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun replaceCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flContainer, fragment)
            commit()
        }
}