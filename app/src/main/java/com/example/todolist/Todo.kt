package com.example.todolist

data class Todo(
    val title: String,                  // saves the task text
    var isChecked: Boolean = false      // indicates whether the task is completed
)