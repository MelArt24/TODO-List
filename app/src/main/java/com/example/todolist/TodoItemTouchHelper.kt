package com.example.todolist

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class TodoItemTouchHelper(private val adapter: TodoAdapter) : ItemTouchHelper.Callback() {

    // Allow dragging elements vertically (up/down)
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
    }

    // The method is called when the element is moved
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        adapter.onItemMove(fromPosition, toPosition) // Updating the position of elements in the adapter
        return true
    }

    // We do not use it
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}
