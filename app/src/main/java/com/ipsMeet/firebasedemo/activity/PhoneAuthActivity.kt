package com.ipsMeet.firebasedemo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ipsMeet.firebasedemo.R
import kotlinx.android.synthetic.main.activity_phone_auth.*
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var phoneNum: String
    private lateinit var auth: FirebaseAuth

    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        val number: EditText = findViewById(R.id.edtxt_phoneNum)

        btnGetOTP.setOnClickListener {
            startLogin(number.text.toString())
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val intent = Intent(this@PhoneAuthActivity, HomeActivity::class.java)
                startActivity(intent)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@PhoneAuthActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationID: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationID, token)
                storedVerificationId = verificationID
                resendToken = token

                val intent = Intent(this@PhoneAuthActivity, OTPActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun startLogin(num: String) {
        if (num.isNotEmpty()) {
            phoneNum = "+91$num"
            sendOTP(phoneNum)
        }
        else {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendOTP(num: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(num)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }
}