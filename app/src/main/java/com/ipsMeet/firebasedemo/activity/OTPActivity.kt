package com.ipsMeet.firebasedemo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.ipsMeet.firebasedemo.R
import kotlinx.android.synthetic.main.activity_otpactivity.*

class OTPActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        auth=FirebaseAuth.getInstance()

        val storedVerificationId= intent.getStringExtra("storedVerificationId")

        btnVerify.setOnClickListener {
            val otp = findViewById<EditText>(R.id.edtxt_otp).text.toString()

            if (otp.isNotEmpty()) {
                val credentials: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)

                auth.signInWithCredential(credentials)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val intent = Intent(this@OTPActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}