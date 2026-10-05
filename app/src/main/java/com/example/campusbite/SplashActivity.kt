package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // Already logged in -> Navigate directly to Main Activity
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // First time or logged out -> Navigate to Login / Auth Screen
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            }
            finish()
        }, 2200)
    }
}