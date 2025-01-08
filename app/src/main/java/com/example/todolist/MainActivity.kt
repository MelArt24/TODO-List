package com.example.todolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

import androidx.recyclerview.widget.ItemTouchHelper


class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var switchThemeButton: Button
    private lateinit var prioritySpinner: Spinner

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchThemeButton = findViewById(R.id.switchThemeButton)
        prioritySpinner = findViewById(R.id.spPriority)

        // Initialize Spinner for priorities
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = adapter
        }

        // Updating a theme using SharedPreferences
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night_mode", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switchThemeButton.text = "Light"
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            switchThemeButton.text = "Dark"
        }

        todoAdapter = TodoAdapter(mutableListOf())

        // Restoring data from SharedPreferences
        val sharedPreferencesData = getSharedPreferences("todo_preferences", Context.MODE_PRIVATE)
        val json = sharedPreferencesData.getString("todo_list", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Todo>>() {}.type
            val savedTodos: List<Todo> = gson.fromJson(json, type)
            todoAdapter.setTodos(savedTodos) // We install the list into the adapter
        }

        val rvTODOItems = findViewById<RecyclerView>(R.id.rvTODOItems)
        rvTODOItems.adapter = todoAdapter
        rvTODOItems.layoutManager = LinearLayoutManager(this)

        // Allows you to enable the ability to drag and drop items in the RecyclerView
        val itemTouchHelper = ItemTouchHelper(TodoItemTouchHelper(todoAdapter))
        itemTouchHelper.attachToRecyclerView(rvTODOItems)

        val bAddTODO = findViewById<Button>(R.id.bAddTODO)
        bAddTODO.setOnClickListener {
            val etTODOTitle = findViewById<EditText>(R.id.etTODOTitle)
            val todoTitle = etTODOTitle.text.toString()
            val selectedPriority = prioritySpinner.selectedItem.toString()

            if(todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle, selectedPriority)
                todoAdapter.addTodo(todo)
                etTODOTitle.text.clear()
            }
        }

        val deleteButton = findViewById<Button>(R.id.bDeleteDoneTODOs)
        deleteButton.setOnClickListener {
            todoAdapter.deleteDoneTodos()
        }

        switchThemeButton.setOnClickListener {
            val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchThemeButton.text = "Dark"
                saveNightModePreference(false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchThemeButton.text = "Light"
                saveNightModePreference(true)
            }
        }
    }

    // Save theme selection in SharedPreferences
    private fun saveNightModePreference(isNightMode: Boolean) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("night_mode", isNightMode)
            apply()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Saving a task list
        outState.putParcelableArrayList("todo_list", ArrayList(todoAdapter.getTodos()))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restoring the task list
        val savedTodos = savedInstanceState.getParcelableArrayList<Todo>("todo_list")
        if (savedTodos != null) {
            todoAdapter.setTodos(savedTodos)
        }
    }

    override fun onPause() {
        super.onPause()
        val gson = Gson()
        val json = gson.toJson(todoAdapter.getTodos()) // Converting a list to JSON
        val sharedPreferencesData = getSharedPreferences("todo_preferences", Context.MODE_PRIVATE)
        with(sharedPreferencesData.edit()) {
            putString("todo_list", json) // Save JSON in SharedPreferences
            apply()
        }
    }
}