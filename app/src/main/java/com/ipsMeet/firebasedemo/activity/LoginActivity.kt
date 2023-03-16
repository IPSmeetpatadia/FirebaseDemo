package com.ipsMeet.firebasedemo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        database = Firebase.database.reference

        login_txt_reg.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = login_edtxt_email.text.toString()
            val pass = login_edtxt_password.text.toString()

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    updaetUI(user)
                } else {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updaetUI(user: FirebaseUser?) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        val signedInUser = auth.currentUser
        if (signedInUser != null) {
            updaetUI(signedInUser)
        }
    }
}