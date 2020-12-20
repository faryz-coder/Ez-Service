package com.bit.ezservice

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bit.ezservice.ui.gallery.Ads
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val favorite: MutableList<Ads>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val favoriteTitle : TextView = itemView.findViewById(R.id.favoriteTitle)
        val favoriteImage : ImageView = itemView.findViewById(R.id.favoriteImage)
        val favoriteLike : ImageView = itemView.findViewById(R.id.favoriteLike)
        val db = Firebase.firestore

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adv = favorite[position]
        holder.favoriteTitle.text = adv.title
        Picasso.get().load(adv.image).into(holder.favoriteImage)
        val dID = adv.dID


        holder.favoriteLike.setOnClickListener {
            holder.db.collection("Profile").document("Saved Ads").collection(dID).document(adv.dataId)
                .delete()
                .addOnSuccessListener {
                    d("bomoh", "Un-favorite")
                }
            d("bomoh", "adv.id: ${adv.dataId}, dID: $dID")
            holder.db.collection("Profile").document("Liked Ads").collection(dID).document(adv.dataId)
                .delete()
                .addOnSuccessListener {
                    d("bomoh", "Un-favorite: Delete Liked Ads")
                }
        }

        holder.favoriteImage.setOnClickListener {
            val bundle = bundleOf("databaseId" to adv.dataId)
            holder.itemView.findNavController().navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
        }
    }

    override fun getItemCount() = favorite.size

}
