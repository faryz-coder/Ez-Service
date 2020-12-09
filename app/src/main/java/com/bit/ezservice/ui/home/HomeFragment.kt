package com.bit.ezservice.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.R

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val categoryRecyclerView : RecyclerView = root.findViewById(R.id.categoriesRecyclerView)

        val categories = mutableListOf<Categories>()
        categories.add(Categories("Hardware and Software"))
        categories.add(Categories("Cleaning"))
        categories.add(Categories("Maintenance"))
        categories.add(Categories("Tutoring"))

        categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
            adapter = CategoryAdapter(categories)
        }


        return root
    }
}


