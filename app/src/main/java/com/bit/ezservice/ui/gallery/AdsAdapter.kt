package com.bit.ezservice.ui.gallery

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.MainActivity
import com.bit.ezservice.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class AdsAdapter(private val ads: MutableList<Ads>) : RecyclerView.Adapter<AdsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.adsTitle)
        val img : ImageView = itemView.findViewById(R.id.adsImg)
        val del : ImageView = itemView.findViewById(R.id.adsDelete)
        val db = Firebase.firestore
        val storage = Firebase.storage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ads_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adv = ads[position]

        holder.title.text = adv.title
        Picasso.get().load(adv.image).into(holder.img)
        val dID = MainActivity().dId

        holder.del.isVisible = adv.frag == "Profile"

        holder.del.setOnClickListener {
            // DELETE INSIDE PROFILE
            holder.db.collection("Profile").document("Ads").collection(dID).document(adv.dataId)
                .delete()
                .addOnSuccessListener {
                    d("bomoh", "deleted: Profile")
                }
            // DELETE ON MAIN
            holder.db.collection("Ads").document(adv.dataId)
                .delete()
                .addOnSuccessListener {
                    d("bomoh", "deleted: Main")
                }

            val storageRef = holder.storage.reference
            val fileRef = storageRef.child(adv.dataId)

            fileRef.delete().addOnSuccessListener {
                d("bomoh", "file Deleted")
            }.addOnFailureListener {
                d("bomoh", "file Deleted: Error")
            }

        }


    }

    override fun getItemCount() = ads.size

}
