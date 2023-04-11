package com.ipsMeet.firebasedemo.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.adapter.ImgListAdapter
import com.ipsMeet.firebasedemo.dataclass.ImgListDataClass
import com.ipsMeet.firebasedemo.dataclass.ViewImgListDataClass
import kotlinx.android.synthetic.main.popup_add_profile.view.*
import java.text.SimpleDateFormat
import java.util.*

class ImageListFragment : Fragment() {

    private lateinit var popupView: View
    private lateinit var imgURI: Uri

    private var listData = arrayListOf<ViewImgListDataClass>()

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference

        val recyclerView = view.findViewById<RecyclerView>(R.id.imgList_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID/profile data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        Log.d("snapshot", snapshot.children.toString())
                        val listedData = item.getValue(ViewImgListDataClass::class.java)
                        listedData?.key = item.key.toString()
                        listData.add(listedData!!)
                        Log.d("listData", listData.toString())
                        Log.d("listedData", listedData.toString())
                        recyclerView.adapter = ImgListAdapter(context!!.applicationContext, listData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_addList -> {
                val builder = AlertDialog.Builder(requireContext())
                val inflater = LayoutInflater.from(context)
                popupView = inflater.inflate(R.layout.popup_add_profile, null)
                builder.setView(popupView)

                val alertDialog = builder.create()
                alertDialog.show()

                val name = popupView.findViewById<EditText>(R.id.profile_edtxt_name)
                val email = popupView.findViewById<EditText>(R.id.profile_edtxt_email)
                val contact = popupView.findViewById<EditText>(R.id.profile_edtxt_phone)
                val address = popupView.findViewById<EditText>(R.id.profile_edtxt_address)

                popupView.profile_img.setOnClickListener {
                    selectImage()
                }

                popupView.btnSaveProfile.setOnClickListener {
                    uploadImage()
                    listData.clear()
                    val profile = ImgListDataClass(
                        imgURI.buildUpon().build().toString(),
                        name.text.toString(),
                        email.text.toString(),
                        contact.text.toString(),
                        address.text.toString()
                    )

                    val userID = FirebaseAuth.getInstance().currentUser!!.uid
                    val key = database.child("profile data").push().key.toString()

                    dbRef.getReference("User/$userID").child("profile data").child(key).setValue(profile)
                    alertDialog.dismiss()
                }

                popupView.btnProfileCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imgURI = data?.data!!
            popupView.profile_img.setImageURI(imgURI)
            Log.d("imgURI", imgURI.toString())
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Data...")
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