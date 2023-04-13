package com.ipsMeet.firebasedemo.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.activity.LoginActivity
import com.ipsMeet.firebasedemo.dataclass.ViewProfileDataClass
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userData: ViewProfileDataClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = view.findViewById<TextView>(R.id.homeFrag_txtName)
        val email = view.findViewById<TextView>(R.id.homeFrag_txtEmail)
        val btnViewData = view.findViewById<Button>(R.id.homeFrag_btn_basicData)
        val btnImgData = view.findViewById<Button>(R.id.homeFrag_btn_imgData)
        val btnPdfData = view.findViewById<Button>(R.id.homeFrag_btn_pdfData)

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userData = snapshot.getValue(ViewProfileDataClass::class.java)!!
                    userData.key = snapshot.key.toString()

                    name.text = userData.name
                    email.text = userData.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error ~ User Details", error.toString())
            }
        })

        homeFrag_cardView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, ProfileFragment(userData))
                .addToBackStack(null)
                .commit()
        }

        btnViewData.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, ListFragment())
                .addToBackStack(null)
                .commit()
        }

        btnImgData.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, ImageListFragment())
                .addToBackStack(null)
                .commit()
        }

        btnPdfData.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, DocumentListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}