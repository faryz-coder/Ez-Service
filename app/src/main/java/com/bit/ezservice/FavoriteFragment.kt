package com.bit.ezservice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.ui.gallery.Ads
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase


class FavoriteFragment : Fragment() {

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.favoriteRecyclerView)
        val dID = (activity as MainActivity).dId
        val favorite = mutableListOf<Ads>()

        fun rv() {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@FavoriteFragment.context)
                adapter = FavoriteAdapter(favorite)
            }
        }

        fun getData() {
            favorite.clear()
            db.collection("Profile").document("Saved Ads").collection(dID)
                .get()
                .addOnSuccessListener {
                    for (result in it) {
                        val dbTitle = result.getField<String>("Title").toString()
                        val dbPhoto = result.getField<String>("Photo Link").toString()
                        val databaseId = result.id
                        favorite.add(Ads(dbPhoto, dbTitle, databaseId, "Favorite", dID))
                    }
                    rv()
                }
        }

        val docRef = db.collection("Profile").document("Saved Ads").collection(dID)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                getData()
            }
        }



        return root
    }
}
