package com.bit.ezservice.ui.home

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
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
        val categoryPhotographer: ImageView = root.findViewById(R.id.categoryPhotographer)
        val categoryElectrical: ImageView = root.findViewById(R.id.categoryElectricalWiring)
        val categoryAircond: ImageView = root.findViewById(R.id.categoryAircondService)
        val categoryPlumbing: ImageView = root.findViewById(R.id.categoryPlumbing)
        val homeSearch: TextView = root.findViewById(R.id.homeSearch)
        val homeLay: ConstraintLayout = root.findViewById(R.id.homeLayout)
        (activity as MainActivity).supportActionBar!!.title = "EZ SERVICE"
        val dID = (activity as MainActivity).dId

        val ads = mutableListOf<Ads>()

        fun selected() {
            selectedRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@HomeFragment.context)
                adapter = AdsAdapter(ads)
            }
        }

        homeLay.setOnClickListener {
            closeKeyBoard(root)
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
                                ads.add(Ads(photoLink, title, databaseId, "Home", dID))
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
            getData("Home Maintenance")
        }
        categoryTutoring.setOnClickListener {
            getData("Lesson")
        }
        categoryPhotographer.setOnClickListener {
            getData("Photographer and Videographer")
        }
        categoryElectrical.setOnClickListener {
            getData("Electrical Wiring")
        }
        categoryAircond.setOnClickListener {
            getData("Aircond Service")
        }
        categoryPlumbing.setOnClickListener {
            getData("Plumbing")
        }

        homeSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                ads.clear()
                if (!s.isNullOrEmpty()) {
                    db.collection("Ads")
                            .get()
                            .addOnSuccessListener {
                                for (result in it) {
                                    val photoLink = result.getField<String>("Photo Link").toString()
                                    val title = result.getField<String>("Title").toString()
                                    val databaseId = result.id

                                    if (title.contains(s.toString())) {
                                        ads.add(Ads(photoLink, title, databaseId, "Home", dID))
                                    }

                                }
                                selected()
                            }
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



        return root
    }
    private fun closeKeyBoard(v : View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}


