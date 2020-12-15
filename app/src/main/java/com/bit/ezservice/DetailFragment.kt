package com.bit.ezservice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class DetailFragment : Fragment() {

    private val db = Firebase.firestore
    var phoneNo: String? = null
    var adsLocation : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_detail, container, false)
        val imageLike = root.findViewById<ImageView>(R.id.imageLike)
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
        val dID = (activity as MainActivity).dId
        val uid = (activity as MainActivity).username
        var statusLike = false

        d("bomoh", "databaseId: $databaseID")
        // CHECK IF ADS ALREADY LIKE OR NOT
        db.collection("Profile").document("Saved Ads").collection(dID).document(databaseID)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    d("bomoh", "ads already liked")
                    imageLike.setImageResource(R.drawable.like)
                } else {
                    d("bomoh", "is empty")
                    imageLike.setImageResource(R.drawable.not_like)
                }
            }

        // GET ADVERTISER DETAILS
        fun getAdvertiser(AdvertiserId: String) {
            db.collection("Account").document(AdvertiserId)
                .get()
                .addOnSuccessListener {
                    advertiserName.text = it.getField<String>("Username")
                    val photoLink = it.getField<String>("Photo Link").toString()
                    if (photoLink != "null") {
                        Picasso.get().load(photoLink).into(
                                advertiserImg
                        )
                    }

                    phoneNo = it.getField<String>("Phone Number").toString()
                }

        }

        // GET CONTENT DETAILS
        db.collection("Ads").document(databaseID)
            .get()
            .addOnSuccessListener {
                title.text = it.getField<String>("Title").toString()
                detailDescription.text = it.getField<String>("Description").toString()
                detailPrice.text = "RM ${it.getField<String>("Price")} "
                adsLocation = it.getField<String>("Location")
                Picasso.get().load(it.getField<String>("Photo Link").toString()).into(adsImg)
                val advertiserId = it.getField<String>("Advertiser").toString()
                getAdvertiser(advertiserId)
            }

        phone.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$phoneNo")
            startActivity(callIntent)
        }

        msg.setOnClickListener {
            val wassapLink = "https://api.whatsapp.com/send?phone=$phoneNo"
            val whatsappIntent = Intent(Intent.ACTION_VIEW)
            whatsappIntent.data = Uri.parse(wassapLink)
            startActivity(whatsappIntent)
        }

        showLocation.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=$adsLocation")
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent)
        }

        fun checkLike(databaseID: String) {
            db.collection("Profile").document("Saved Ads").collection(dID).document(databaseID)
                .get()
                .addOnSuccessListener { results ->
                    if (results.exists()) {
                        d("bomoh", "ads already liked")
                        val adId = results.getField<String>("Advertiser").toString()
                        db.collection("Profile").document("Saved Ads").collection(dID).document(databaseID)
                            .delete()
                            .addOnSuccessListener {
                                // DELETE LIKED ADS
                                db.collection("Profile").document("Liked Ads").collection(adId).document(databaseID)
                                    .delete()
                                    .addOnSuccessListener {
                                        imageLike.setImageResource(R.drawable.not_like)
                                    }
                            }

                    } else {
                        d("bomoh", "is empty")
                        db.collection("Ads").document(databaseID)
                            .get()
                            .addOnSuccessListener { document ->
                                val data = hashMapOf(
                                    "Advertiser" to document.getField<String>("Advertiser"),
                                    "Category" to document.getField<String>("Category"),
                                    "Description" to document.getField<String>("Description"),
                                    "Latitude" to document.getField<String>("Latitude"),
                                    "Longitude" to document.getField<String>("Longitude"),
                                    "Photo Link" to document.getField<String>("Photo Link"),
                                    "Price" to document.getField<String>("Price"),
                                    "Title" to document.getField<String>("Title")
                                )

                                db.collection("Profile").document("Saved Ads").collection(dID).document(databaseID)
                                    .set(data)
                                    .addOnSuccessListener {
                                        // ADD TO LIKED ADS DB
                                        val advertiser = document.getField<String>("Advertiser").toString()

                                        val data2 = hashMapOf(
                                            "Name" to uid,
                                            "Notify" to false
                                        )
                                        db.collection("Profile").document("Liked Ads").collection(advertiser).document(databaseID)
                                            .set(data2)
                                            .addOnSuccessListener {
                                                imageLike.setImageResource(R.drawable.like)
                                            }
                                    }
                            }
                    }
                }

                .addOnFailureListener {
                    d("bomoh", "check like fail: $it")
                }
        }
        imageLike.setOnClickListener {
            imageLike.setImageResource(R.drawable.like)
            checkLike(databaseID)
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}