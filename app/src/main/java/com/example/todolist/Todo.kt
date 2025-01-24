package com.example.todolist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Todo(
    var title: String,                  // saves the task text
    val priority: String = "Low",       // shows priority of task
    var isChecked: Boolean = false      // indicates whether the task is completed
) : Parcelable