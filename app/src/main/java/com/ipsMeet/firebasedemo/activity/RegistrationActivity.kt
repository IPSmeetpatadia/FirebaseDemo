package com.ipsMeet.firebasedemo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.dataclass.UserDataClass
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = Firebase.auth
        database = Firebase.database.reference

        btnReg.setOnClickListener {
            val email = reg_edtxt_email.text.toString()
            val pass = reg_edtxt_password.text.toString()

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            saveUser()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        ?.addOnFailureListener {
                            Log.d("FAIL!!", it.toString())
                        }
                }
                else {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        reg_txt_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUser() {
        val addUser = UserDataClass(
            name = reg_edtxt_name.text.toString(),
            email = reg_edtxt_email.text.toString(),
            password = reg_edtxt_password.text.toString()
        )

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).setValue(addUser)
    }
}