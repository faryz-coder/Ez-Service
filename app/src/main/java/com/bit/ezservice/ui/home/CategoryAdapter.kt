package com.bit.ezservice.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.R

class CategoryAdapter(private val categories: MutableList<Categories>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryTitle : TextView = itemView.findViewById(R.id.categoryTitle)
        val categoryLogo: ImageView = itemView.findViewById(R.id.categoryLogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTitle.text = category.title

        when (category.title) {
            "Hardware and Software" -> {
                holder.categoryLogo.setImageResource(R.drawable.computer_hardware)
            }
            "Cleaning" -> {
                holder.categoryLogo.setImageResource(R.drawable.cleaning)
            }
            "Maintenance" -> {
                holder.categoryLogo.setImageResource(R.drawable.maintanance)
            }
            "Tutoring" -> {
                holder.categoryLogo.setImageResource(R.drawable.tutoring)
            }
        }
    }

    override fun getItemCount() = categories.size

}
