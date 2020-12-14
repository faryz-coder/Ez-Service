package com.bit.ezservice.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.MainActivity
import com.bit.ezservice.R
import com.bit.ezservice.ui.gallery.Ads
import com.bit.ezservice.ui.gallery.AdsAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    private val db = Firebase.firestore
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val selectedRecyclerView : RecyclerView = root.findViewById(R.id.selectedRecyclerView)
        val categoryHardware: ImageView = root.findViewById(R.id.categoryHardware)
        val categoryCleaning: ImageView = root.findViewById(R.id.categoryCleaning)
        val categoryMaintenance: ImageView = root.findViewById(R.id.categoryMaintenance)
        val categoryTutoring: ImageView = root.findViewById(R.id.categoryTutoring)
        val homeSearch: TextView = root.findViewById(R.id.homeSearch)
        (activity as MainActivity).supportActionBar!!.title = "EZ SERVICE"


        val ads = mutableListOf<Ads>()

        fun selected() {
            selectedRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@HomeFragment.context)
                adapter = AdsAdapter(ads)
            }
        }

        fun getData(info: String) {
            ads.clear()
            db.collection("Ads")
                    .get()
                    .addOnSuccessListener {
                        for (result in it) {
                            val photoLink = result.getField<String>("Photo Link").toString()
                            val title = result.getField<String>("Title").toString()
                            val databaseId = result.id
                            val cate = result.getField<String>("Category").toString()

                            if (cate.contains(info)) {
                                ads.add(Ads(photoLink, title, databaseId, "Home"))
                            }

                        }
                        selected()
                    }
        }

        categoryHardware.setOnClickListener {
            getData("Hardware")
        }
        categoryCleaning.setOnClickListener {
            getData("Cleaning")
        }
        categoryMaintenance.setOnClickListener {
            getData("Maintenance")
        }
        categoryTutoring.setOnClickListener {
            getData("Tutoring")
        }

        homeSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                ads.clear()
                db.collection("Ads")
                    .get()
                    .addOnSuccessListener {
                        for (result in it) {
                            val photoLink = result.getField<String>("Photo Link").toString()
                            val title = result.getField<String>("Title").toString()
                            val databaseId = result.id

                            if (title.contains(s.toString())) {
                                ads.add(Ads(photoLink, title, databaseId, "Home"))
                            }

                        }
                        selected()
                    }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



        return root
    }
}


