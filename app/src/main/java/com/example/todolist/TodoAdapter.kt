package com.example.todolist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast

// TodoAdapter gets a list of tasks (todos) and inherits from
// RecyclerView.Adapter, which provides the connection between
// the data and the list

class TodoAdapter(
    private val todos: MutableList<Todo>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // TodoViewHolder describes the view of a single list item
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTodoTitle: TextView = itemView.findViewById(R.id.tvTodoTitle)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val tvPriorityIndicator: TextView = itemView.findViewById(R.id.tvPriorityIndicator)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
    }

    // The method creates new views for the list, but this only happens
    // when a new view is needed. If the RecyclerView can recycle
    // existing views, onCreateViewHolder() is not called
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.todo_item,
                parent,
                false
            )
        )
    }

    // Adds tasks to the todos list
    fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    // Removes all items from the list where isChecked == true
    // notifyDataSetChanged updates the entire list
    @SuppressLint("NotifyDataSetChanged")
    fun deleteDoneTodos() {
        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }

    // Adds or removes strikethrough text from the task name
    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        if(isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun getPriorityColor(priority: String?, itemView: View): Int {
        return when (priority) {
            "High" -> ContextCompat.getColor(itemView.context, R.color.priority_high)
            "Medium" -> ContextCompat.getColor(itemView.context, R.color.priority_medium)
            "Low" -> ContextCompat.getColor(itemView.context, R.color.priority_low)
            else -> ContextCompat.getColor(itemView.context, R.color.priority_default)
        }
    }

    // Data binding to the element
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todos[position]
        holder.apply {
            tvTodoTitle.text = currentTodo.title
            cbDone.isChecked = currentTodo.isChecked
            toggleStrikeThrough(tvTodoTitle, currentTodo.isChecked)

            tvPriorityIndicator.setBackgroundColor(
                getPriorityColor(currentTodo.priority, itemView)
            )

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                currentTodo.isChecked = !currentTodo.isChecked
            }

            btnEdit.setOnClickListener {
                // Create an editing dialog
                val context = it.context
                val editText = EditText(context).apply {
                    setText(currentTodo.title)
                    hint = "Edit task"
                }

                AlertDialog.Builder(context)
                    .setTitle("Edit Task")
                    .setView(editText)
                    .setPositiveButton("Save") { _, _ ->
                        val updatedTitle = editText.text.toString()
                        if (updatedTitle.isNotBlank()) {
                            currentTodo.title = updatedTitle
                            notifyItemChanged(position) // Updating an item in a list
                        } else {
                            // Show a message if the input is empty
                            Toast.makeText(context, "The task title cannot be empty!", Toast.LENGTH_LONG).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    // Returns the number of tasks in the list
    override fun getItemCount(): Int {
        return todos.size
    }

    // getting a list of tasks
    fun getTodos(): List<Todo> {
        return todos
    }

    // setting a list of tasks
    @SuppressLint("NotifyDataSetChanged")
    fun setTodos(newTodos: List<Todo>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }

    // The method is responsible for changing the order of items in the list after dragging
    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition != toPosition) {
            val item = todos.removeAt(fromPosition)
            todos.add(toPosition, item)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

}