package com.bit.ezservice

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class DetailFragment : Fragment() {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail, container, false)
        val advertiserName = root.findViewById<TextView>(R.id.detailAdvertiserName)
        val advertiserImg = root.findViewById<ImageView>(R.id.advertiserImg)
        val title = root.findViewById<TextView>(R.id.detailTitle)
        val adsImg = root.findViewById<ImageView>(R.id.detailAdsImg)
        val msg = root.findViewById<ImageView>(R.id.detailMsg)
        val phone = root.findViewById<ImageView>(R.id.detailPhone)
        val showLocation = root.findViewById<ImageView>(R.id.detailLoc)
        val detailDescription = root.findViewById<TextView>(R.id.detailDescription)
        val detailPrice = root.findViewById<TextView>(R.id.detailPrice)
        val databaseID = arguments?.getString("databaseId").toString()

        d("bomoh", "databaseId: $databaseID")

        // GET ADVERTISER DETAILS
        fun getAdvertiser(AdvertiserId: String) {
            db.collection("Account").document(AdvertiserId)
                .get()
                .addOnSuccessListener {
                    advertiserName.text = it.getField<String>("Username")
                    Picasso.get().load(it.getField<String>("Photo Link").toString()).into(advertiserImg)
                }

        }

        // GET CONTENT DETAILS
        db.collection("Ads").document(databaseID)
            .get()
            .addOnSuccessListener {
                title.text = it.getField<String>("Title").toString()
                detailDescription.text = it.getField<String>("Description").toString()
                detailPrice.text = it.getField<String>("Price")
                Picasso.get().load(it.getField<String>("Photo Link").toString()).into(adsImg)
                val advertiserId = it.getField<String>("Advertiser").toString()
                getAdvertiser(advertiserId)
            }


        return root
    }

}