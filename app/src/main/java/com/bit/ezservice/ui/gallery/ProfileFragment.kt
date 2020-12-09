package com.bit.ezservice.ui.gallery

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.MainActivity
import com.bit.ezservice.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val recyclerView : RecyclerView = root.findViewById(R.id.myProfileRecyclerView)
        val img : ImageView = root.findViewById(R.id.currentProfileImg)
        val username : TextView = root.findViewById(R.id.textUsername)
        val dId = (activity as MainActivity).dId

        db.collection("Account").document(dId)
            .get()
            .addOnSuccessListener {
                val name = it.getField<String>("Username").toString()
                val imgUrl = it.getField<String>("Photo Link").toString()

                Picasso.get().load(imgUrl).into(img)
                username.text = name
            }

        val ads = mutableListOf<Ads>()
        fun rv() {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@ProfileFragment.context)
                adapter = AdsAdapter(ads)
            }
        }

        fun getData() {
            ads.clear()
            db.collection("Profile").document("Ads").collection(dId)
                .get()
                .addOnSuccessListener {
                    for (result in it) {
                        val title = result.getField<String>("Title").toString()
                        val photoLink = result.getField<String>("Photo Link").toString()
                        val databaseId = result.id
                        ads.add(Ads(photoLink, title, databaseId))
                    }
                    rv()
                }
        }

        val docRef = db.collection("Profile").document("Ads").collection(dId)
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                getData()
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_createAdsFragment)
        }

        view.findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_editProfileFragment)
        }
    }
}