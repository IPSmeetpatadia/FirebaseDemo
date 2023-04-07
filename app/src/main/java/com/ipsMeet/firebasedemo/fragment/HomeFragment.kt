package com.ipsMeet.firebasedemo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.activity.LoginActivity

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        requireActivity().title = "Home"

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

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).get()
            .addOnSuccessListener {
                name.text = it.child("name").value.toString()
                email.text = it.child("email").value.toString()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            }

        btnViewData.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, ListFragment())
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