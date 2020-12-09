package com.bit.ezservice

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class EditProfileFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val editLayout : ConstraintLayout = root.findViewById(R.id.editProfile_layout)
        val buttonSave : Button = root.findViewById(R.id.buttonSave)
        val dID = (activity as MainActivity).dId

        // GET PROFILE INFO AND DISPLAY
        db.collection("Account").document(dID)
            .get()
            .addOnSuccessListener {
                val name: TextView = root.findViewById(R.id.editProfileName)
                val email: TextView = root.findViewById(R.id.editProfileEmail)
                val phoneNo: TextView = root.findViewById(R.id.editProfilePhoneNo)
                val location: TextView = root.findViewById(R.id.editProfileLocation)
                val img = root.findViewById<ImageView>(R.id.editProfileImage)
                val currentName: TextView = root.findViewById(R.id.currentProfileName)

                currentName.text = it.getField<String>("Username").toString()
                name.text = it.getField<String>("Username").toString()
                email.text = it.getField<String>("Email").toString()
                phoneNo.text = it.getField<String>("Phone Number").toString()
                if (it.getField<String>("Location") != null) {
                    location.text = it.getField<String>("Location").toString()
                }
                if (it.getField<String>("Photo Link") != null) {
                    img.setImageURI(it.getField<String>("Photo Link").toString().toUri())
                }
            }

        // BUTTON SAVE PRESSED
        buttonSave.setOnClickListener {
            val name: TextView = root.findViewById(R.id.editProfileName)
            val email: TextView = root.findViewById(R.id.editProfileEmail)
            val phoneNo: TextView = root.findViewById(R.id.editProfilePhoneNo)
            val location: TextView = root.findViewById(R.id.editProfileLocation)

            saveStat(name,email,phoneNo,location, dID)
        }

        editLayout.setOnClickListener {
            closeKeyBoard(root)
        }


        return root
    }

    private fun saveStat(name: TextView, email: TextView, phoneNo: TextView, location: TextView, dID: String) {
        fun updateData() {
            val data = hashMapOf(
                "Username" to name.text.toString(),
                "Photo Link" to imageUploadURL,
                "Phone Number" to phoneNo.text.toString(),
                "Location" to location.text.toString()
            )

            db.collection("Account").document(dID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Snackbar.make(requireView(), "Profile Updated", Snackbar.LENGTH_SHORT).show()
                    requireView().findNavController().popBackStack()
                }
                .addOnFailureListener {
                    Snackbar.make(requireView(), "Profile Failed to Update", Snackbar.LENGTH_SHORT).show()
                }
        }

        if (imageUploadURL!= null) {
            val storageRef = storage.reference.child(dID)
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
                    updateData()

                } else {
                    d("bomoh", "image Upload Failed")
                    return@addOnCompleteListener
                }
            }
        } else {
            updateData()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        val changeImg : FloatingActionButton = view.findViewById(R.id.floatChangeImg)
        changeImg.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        val locationText: TextView = view.findViewById(R.id.editProfileLocation)
        val getLocation : ImageView = view.findViewById(R.id.editProfileGetLocation)
        getLocation.setOnClickListener {
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
                            loc = locations.getAddressLine(0)
                            lat = locations.latitude.toString()
                            long = locations.longitude.toString()
                            d("bomoh", "lat:$lat , lon:$long")
                            locationText.text = loc.toString()
                        }

                    }
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage) {
            val img = view?.findViewById<ImageView>(R.id.editProfileImage)
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