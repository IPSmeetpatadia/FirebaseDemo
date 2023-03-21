package com.ipsMeet.firebasedemo.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.fragment.ListFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).get()
            .addOnSuccessListener {
                home_txt_name.text = it.child("name").value.toString()
                home_txt_email.text = it.child("email").value.toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        home_btnGetList.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.home_layout, ListFragment()).addToBackStack("Home").commit()
            /*
            val frameLayout = FrameLayout(this)
            frameLayout.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            frameLayout.id = (R.id.home_layout)
            setContentView(frameLayout)
            supportFragmentManager.beginTransaction().replace(R.id.home_layout, ListFragment()).addToBackStack("Home")
                .commit()

 */
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}