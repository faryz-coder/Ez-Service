package com.bit.ezservice

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import java.util.jar.Manifest

class CreateAdsFragment : Fragment() {

    private val pickImage = 100
    private var imageUri : Uri? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private var loc : String? = null
    private var lat : String? = null
    private var long : String? = null
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private var imageUploadURL : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_create_ads, container, false)

        val createAdLayout: ConstraintLayout = root.findViewById(R.id.createAd_layout)
        val buttonCreateAd: Button = root.findViewById(R.id.buttonCreateAd)
        val spinner: Spinner = root.findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(root.context, R.array.category, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        buttonCreateAd.setOnClickListener {
            val title : TextView = root.findViewById(R.id.inputTitle)
            val description: TextView = root.findViewById(R.id.inputDescription)
            val price : TextView = root.findViewById(R.id.inputPrice)
            val category = spinner.selectedItem.toString()
            createAd(title, description, price, category)
        }

        createAdLayout.setOnClickListener {
            closeKeyBoard(root)
        }

        return root
    }

    private fun createAd(title: TextView, description: TextView, price: TextView, category: String) {
        if (!valid(title, description, price, category)) {
            return
        }
        val dID = (activity as MainActivity).dId

        val data = hashMapOf(
            "Title" to title.text.toString(),
            "Description" to description.text.toString(),
            "Category" to category,
            "Price" to price.text.toString(),
            "Location" to loc,
            "Latitude" to lat,
            "Longitude" to long,
            "Advertiser" to dID
        )
        db.collection("Profile").document("Ads").collection(dID)
            .add(data)
            .addOnSuccessListener {
                Snackbar.make(requireView(), "Ads has been Posted", Snackbar.LENGTH_SHORT).show()
                requireView().findNavController().popBackStack()
                val documentId = it.id

                //upload image
                val storageRef = storage.reference.child(documentId)
                val uploadImg = storageRef.putFile(imageUri!!)

                val urlTask = uploadImg.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageUploadURL = task.result.toString()
                        d("bomoh", "image Upload Success")
                        val imgData = hashMapOf(
                            "Photo Link" to imageUploadURL
                        )
                        db.collection("Profile").document("Ads").collection(dID).document(documentId)
                            .set(imgData, SetOptions.merge())

                        val data = hashMapOf(
                            "Title" to title.text.toString(),
                            "Description" to description.text.toString(),
                            "Category" to category,
                            "Price" to price.text.toString(),
                            "Location" to loc,
                            "Latitude" to lat,
                            "Longitude" to long,
                            "Photo Link" to imageUploadURL,
                            "Advertiser" to dID
                        )

                        db.collection("Ads").document(documentId)
                            .set(data, SetOptions.merge())


                    } else {
                        d("bomoh", "image Upload Failed")
                        return@addOnCompleteListener
                    }
                }
            }
            .addOnFailureListener {
                Snackbar.make(requireView(), "Ads Failed", Snackbar.LENGTH_SHORT).show()
            }



    }

    private fun valid(title: TextView, description: TextView, price: TextView, category: String): Boolean {
        var valid = true

        if (title.text.isNullOrEmpty()) {
            valid = false
            title.error = "Enter Title"
        } else {
            title.error = null
        }

        if (description.text.isNullOrEmpty()) {
            valid = false
            description.error = "Enter Description"
        } else {
            description.error = null
        }

        if (price.text.isNullOrEmpty()) {
            valid = false
            price.error = "Enter Price"
        } else {
            price.error = null
        }

        if (category == "Category") {
            valid = false
            view?.let {
                Snackbar.make(it, "Select Category", Snackbar.LENGTH_SHORT).show()
            }
        }

        if (imageUri == null) {
            valid = false
            view?.let {
                Snackbar.make(it, "Upload Photo", Snackbar.LENGTH_SHORT).show()
            }
        }

        if (loc == null) {
            valid = false
            view?.let {
                Snackbar.make(it, "Press Get Location", Snackbar.LENGTH_SHORT).show()
            }
        }

        return valid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        view.findViewById<Button>(R.id.buttonUploadPhoto).setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        view.findViewById<ImageView>(R.id.getLocation).setOnClickListener {
            d("bomoh", "getlocation:clicked")
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {}
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    d("bomoh", "location: $it")
                    if (it != null) {
                        val geocoder = Geocoder(view.context, Locale.getDefault())
                        val location = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        d("bomoh", "location: $location")
                        for (locations in location) {
                            val addLine = locations.getAddressLine(0)
                            val local = locations.locality
                            loc = locations.getAddressLine(0)
                            lat = locations.latitude.toString()
                            long = locations.longitude.toString()
                            d("bomoh", "lat:$lat , lon:$long")
                            Snackbar.make(requireView(), addLine, Snackbar.LENGTH_LONG).show()
                            view.findViewById<ImageView>(R.id.getLocation).setColorFilter(Color.BLUE)
                        }

                    }
                }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage) {
            val img = view?.findViewById<ImageView>(R.id.imageSelected)
            imageUri = data?.data
            d("bomoh", "image uri: $imageUri")
            d("bomoh", "data: $data")
            img?.setImageURI(imageUri)
        }
    }

    private fun closeKeyBoard(v : View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}