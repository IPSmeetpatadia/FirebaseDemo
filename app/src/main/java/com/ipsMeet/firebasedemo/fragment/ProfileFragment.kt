package com.ipsMeet.firebasedemo.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.dataclass.ViewProfileDataClass
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profile_img
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment(private val userData: ViewProfileDataClass) : Fragment() {

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()
    private var key = ""
    private lateinit var imgURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        key = userData.key
        Glide.with(requireActivity()).load(userData.profileImg!!.toUri()).into(profile_img)
        profile_name.setText(userData.name)
        profile_email.setText(userData.email)

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        profile_img.setOnClickListener {
            selectImage()
        }

        btnSaveProfile.setOnClickListener {
            uploadImage()
            dbRef.getReference("User/$userID").apply {
                child("profileImg").setValue(imgURI.buildUpon().build().toString())
                child("name").setValue(profile_name.text.toString())
                child("email").setValue(profile_email.text.toString())
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imgURI = data?.data!!
            profile_img.setImageURI(imgURI)
            Log.d("imgURI", imgURI.toString())
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Updating Profile")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val fileName = SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault()).format(Date())
        val storageReference = FirebaseStorage.getInstance().getReference("Images/*$fileName")

        storageReference.putFile(imgURI)
            .addOnSuccessListener {
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
    }

}