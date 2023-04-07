package com.ipsMeet.firebasedemo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.fragment.HomeFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_layout, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}