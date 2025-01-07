package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoAdapter(mutableListOf())

        // Restoring data from SharedPreferences
        val sharedPreferences = getSharedPreferences("todo_preferences", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("todo_list", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Todo>>() {}.type
            val savedTodos: List<Todo> = gson.fromJson(json, type)
            todoAdapter.setTodos(savedTodos) // We install the list into the adapter
        }

        val rvTODOItems = findViewById<RecyclerView>(R.id.rvTODOItems)
        rvTODOItems.adapter = todoAdapter
        rvTODOItems.layoutManager = LinearLayoutManager(this)

        val bAddTODO = findViewById<Button>(R.id.bAddTODO)
        bAddTODO.setOnClickListener {
            val etTODOTitle = findViewById<EditText>(R.id.etTODOTitle)
            val todoTitle = etTODOTitle.text.toString()
            if(todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                etTODOTitle.text.clear()
            }
        }
        val deleteButton = findViewById<Button>(R.id.bDeleteDoneTODOs)
        deleteButton.setOnClickListener {
            todoAdapter.deleteDoneTodos()
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
        val sharedPreferences = getSharedPreferences("todo_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("todo_list", json) // Save JSON in SharedPreferences
            apply()
        }
    }
}