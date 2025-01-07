package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoAdapter(mutableListOf())

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

}