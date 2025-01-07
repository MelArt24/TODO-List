package com.example.todolist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Todo(
    val title: String,                  // saves the task text
    var isChecked: Boolean = false      // indicates whether the task is completed
) : Parcelable